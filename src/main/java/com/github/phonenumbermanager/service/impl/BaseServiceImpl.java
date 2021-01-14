package com.github.phonenumbermanager.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.phonenumbermanager.dao.*;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.DateUtils;
import com.github.phonenumbermanager.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.*;

/**
 * 基础Service实现
 *
 * @param <T> SERVICE泛型
 * @author 廿二月的天
 */
abstract class BaseServiceImpl<T> implements BaseService<T> {
    private BaseDao<T> baseDao;
    @Resource
    protected SystemUserDao systemUserDao;
    @Resource
    protected DormitoryManagerDao dormitoryManagerDao;
    @Resource
    protected UserRoleDao userRoleDao;
    @Resource
    protected UserPrivilegeDao userPrivilegeDao;
    @Resource
    protected UserRolePrivilegeDao userRolePrivilegeDao;
    @Resource
    protected SubdistrictDao subdistrictDao;
    @Resource
    protected CommunityDao communityDao;
    @Resource
    protected CommunityResidentDao communityResidentDao;
    @Resource
    protected ConfigurationDao configurationDao;
    @Resource
    protected SubcontractorDao subcontractorDao;
    List<Subcontractor> subcontractors;
    Map<String, Long> communityMap;


    /**
     * 最先运行的方法，自动加载对应类型的DAO
     *
     * @throws NoSuchFieldException   找不到参数异常
     * @throws IllegalAccessException 错误转换异常
     */
    @PostConstruct
    private void initBaseDao() throws NoSuchFieldException, IllegalAccessException {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        String className = clazz.getSimpleName();
        String localField = className.substring(0, 1).toLowerCase() + className.substring(1) + "Dao";
        Field field = this.getClass().getSuperclass().getDeclaredField(localField);
        Field baseField = this.getClass().getSuperclass().getDeclaredField("baseDao");
        baseField.set(this, field.get(this));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long create(T obj) {
        return baseDao.insert(obj);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long delete(Serializable id) {
        return baseDao.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long delete(String name) {
        return baseDao.deleteByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long update(T obj) {
        return baseDao.update(obj);
    }

    @Override
    public List<T> find() {
        return baseDao.selectAll();
    }

    @Override
    public Map<String, Object> find(Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectAll();
        return find(data);
    }

    @Override
    public T find(Serializable id) {
        return baseDao.selectById(id);
    }

    @Override
    public Map<String, Object> find(String name, Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectByName(name);
        return find(data);
    }

    @Override
    public Map<String, Object> find(T object, Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectByObject(object);
        return find(data);
    }

    @Override
    public List<T> findForIdAndName() {
        return baseDao.selectForIdAndName();
    }

    @Override
    public List<T> find(T object) {
        return baseDao.selectByObject(object);
    }

    @Override
    public List<T> find(String nameAddress, Serializable id, Serializable subdistrictId) {
        return baseDao.selectByNameAndAddress(nameAddress, id, subdistrictId);
    }

    @Override
    public List<T> find(List<String> phones, Serializable id, Serializable subdistrictId) {
        return baseDao.selectByPhones(phones, id, subdistrictId);
    }

    @Override
    public Map<String, String> findPartStatHead() {
        return null;
    }

    @Override
    public Map<String, Object> find(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public Map<String, Object> findCorrelation(Integer pageNumber, Integer pageDataSize) {
        return null;
    }

    @Override
    public long create(Workbook workbook, Serializable subdistrictId, Map<String, Object> configurationsMap) throws Exception {
        return 0;
    }

    @Override
    public Map<String, Object> findCorrelation(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber, Integer pageDataSize) {
        List<Serializable> companyIds = findCommunityIds(systemUser, communityCompanyType, subdistrictCompanyType, pageNumber, pageDataSize);
        List<T> data = baseDao.selectAndCommunityByCommunityIds(companyIds);
        return find(data);
    }

    /**
     * 配置PageHelper
     *
     * @param pageNumber   分页页码
     * @param pageDataSize 每页显示数量
     */
    void setPageHelper(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        PageHelper.startPage(pageNumber, pageDataSize);
    }

    /**
     * 查找对象的方法
     *
     * @param data 对象集合
     * @return 查找到的对象集合与分页对象
     */
    Map<String, Object> find(List<T> data) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("data", data);
        map.put("pageInfo", new PageInfo<>(data));
        return map;
    }

    /**
     * 查找社区编号
     *
     * @param systemUser             系统用户对象
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param pageNumber             页面页码
     * @param pageDataSize           页面展示数据数量
     * @return 社区编号数组
     */
    List<Serializable> findCommunityIds(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Integer companyType = systemUser.getCompanyType();
        Long companyId = systemUser.getCompanyId();
        List<Serializable> communityIds = new ArrayList<>();
        if (companyType.equals(communityCompanyType)) {
            communityIds.add(companyId);
        } else if (companyType.equals(subdistrictCompanyType)) {
            List<Community> communities = communityDao.selectCorrelationBySubdistrictId(companyId);
            for (Community community : communities) {
                communityIds.add(community.getId());
            }
        } else {
            communityIds = null;
        }
        PageHelper.startPage(pageNumber, pageDataSize);
        return communityIds;
    }

    /**
     * 转换单元格字符串
     *
     * @param cell 需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    String convertCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return CommonUtils.qj2bj(CommonUtils.replaceBlank(String.valueOf(ExcelUtils.getCellValue(cell, CellType.STRING))));
    }

    /**
     * 转换单元格数字类型
     *
     * @param cell 需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    Integer convertCell(Cell cell) {
        return Integer.parseInt(String.valueOf(ExcelUtils.getCellValue(cell, CellType.NUMERIC)));
    }

    /**
     * 分包人处理
     *
     * @param name           分包人姓名
     * @param mobile         分包人联系方式
     * @param subcontractors 从数据库查询的分包人集合对象
     * @param communityId    所属社区编号
     * @return 分包人编号
     */
    @Transactional(rollbackFor = Exception.class)
    Long addSubcontractorHandler(String name, String mobile, List<Subcontractor> subcontractors, Serializable communityId) {
        Long id = null;
        Timestamp timestamp = DateUtils.getTimestamp(new Date());
        for (Subcontractor subcontractor : subcontractors) {
            if (name.equals(subcontractor.getName())) {
                id = subcontractor.getId();
                if (StringUtils.isEmpty(subcontractor.getMobile())) {
                    subcontractor.setMobile(mobile);
                    subcontractor.setUpdateTime(timestamp);
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    subcontractorDao.update(subcontractor);
                }
                break;
            }
        }
        if (id == null) {
            Subcontractor newSubcontractor = new Subcontractor();
            newSubcontractor.setName(name);
            newSubcontractor.setMobile(mobile);
            newSubcontractor.setCommunityId((Long) communityId);
            newSubcontractor.setCreateTime(timestamp);
            newSubcontractor.setUpdateTime(timestamp);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            subcontractorDao.insert(newSubcontractor);
            subcontractors.add(newSubcontractor);
        }
        return id;
    }

    /**
     * 获取社区编号
     *
     * @param communityMap  社区集合
     * @param communityName 社区名称
     * @return 社区编号
     */
    Long getCommunityId(Map<String, Long> communityMap, String communityName) {
        Long communityId;
        if (!communityMap.containsKey(communityName)) {
            communityId = (Long) communityDao.selectIdByName(communityName);
            if (communityId == null) {
                throw new BusinessException("找不到社区名称为“" + communityName + "”的社区，请创建此社区后，重新导入！");
            }
            communityMap.put(communityName, communityId);
        } else {
            communityId = communityMap.get(communityName);
        }
        return communityId;
    }

    /**
     * 设置社区变量
     *
     * @param subdistrictId 街道编号
     */
    void setCommunityVariables(Serializable subdistrictId) {
        subcontractors = subcontractorDao.selectAll();
        List<Community> communities = communityDao.selectBySubdistrictId(subdistrictId);
        communityMap = new HashMap<>(communities.size() + 1);
    }

    /**
     * 柱状图数据处理
     *
     * @param label        饼图图例
     * @param companyLabel 柱状图横坐标文字
     * @param formatter    格式文字
     * @param object       饼图数据内容
     * @return 处理后的对象
     */
    Map<String, Object> barChartDataHandler(String label, String companyLabel, String formatter, LinkedList<Map<String, Object>> object) {
        Map<String, Object> barChartMap = new HashMap<>(3);
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> data = new HashMap<>(3);
        List<String> titleLabel = new ArrayList<>();
        for (Map<String, Object> residentCount : object) {
            Map<String, Object> row = new HashMap<>(3);
            row.put(companyLabel, residentCount.get("name"));
            titleLabel.add((String) residentCount.get("name"));
            row.put(label, residentCount.get("value"));
            rows.add(row);
        }
        columns.add(companyLabel);
        columns.add(label);
        barChartMap.put("columns", columns);
        barChartMap.put("rows", rows);
        data.put("data", barChartMap);
        data.put("titleLabel", titleLabel);
        data.put("formatter", formatter);
        return data;
    }

    /**
     * 获取单位类型和单位编号
     *
     * @param systemUser   登录的系统用户对象
     * @param companyId    查找的范围单位的编号
     * @param companyType  查找的范围单位的类别编号
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示的数量
     * @return 单位类型和单位编号集合
     */
    Map<String, Object> getCompany(SystemUser systemUser, Serializable companyId, Serializable companyType, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        if (companyType == null || companyId == null) {
            companyType = systemUser.getCompanyType();
            companyId = systemUser.getCompanyId();
        }
        Map<String, Object> company = new HashMap<>(3);
        company.put("companyId", companyId);
        company.put("companyType", companyType);
        PageHelper.startPage(pageNumber, pageDataSize);
        return company;
    }
}
