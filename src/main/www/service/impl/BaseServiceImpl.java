package www.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import utils.CommonUtil;
import utils.ExcelUtil;
import www.dao.*;
import www.entity.Community;
import www.entity.Subcontractor;
import www.entity.SystemUser;
import www.service.BaseService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 基础Service实现
 *
 * @param <T> SERVICE泛型
 * @author 廿二月的天
 */
public class BaseServiceImpl<T> implements BaseService<T> {
    BaseDao<T> baseDao;
    @Resource
    protected DormitoryManagersDao dormitoryManagersDao;
    @Resource
    protected SystemUsersDao systemUsersDao;
    @Resource
    protected UserRolesDao userRolesDao;
    @Resource
    protected UserPrivilegesDao userPrivilegesDao;
    @Resource
    protected UserRolePrivilegesDao userRolePrivilegesDao;
    @Resource
    protected SubdistrictsDao subdistrictsDao;
    @Resource
    protected CommunitiesDao communitiesDao;
    @Resource
    protected CommunityResidentsDao communityResidentsDao;
    @Resource
    protected ConfigurationsDao configurationsDao;
    @Resource
    protected SubcontractorsDao subcontractorsDao;
    List<Subcontractor> subcontractors;
    Map<String, Long> communityMap;
    @Resource(name = "transactionManager")
    private DataSourceTransactionManager transactionManager;

    /**
     * 最先运行的方法，自动加载对应类型的DAO
     *
     * @throws Exception SERVICE层异常
     */
    @PostConstruct
    private void initBaseDao() throws Exception {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        String className = clazz.getSimpleName();
        StringBuilder localField = new StringBuilder();
        localField.append(className.substring(0, 1).toLowerCase());
        if ("y".equals(className.substring(className.length() - 1))) {
            localField.append(className, 1, className.length() - 1).append("ies");
        } else {
            localField.append(className.substring(1)).append("s");
        }
        localField.append("Dao");
        Field field = this.getClass().getSuperclass().getDeclaredField(localField.toString());
        Field baseField = this.getClass().getSuperclass().getDeclaredField("baseDao");
        baseField.set(this, field.get(this));
    }

    @Override
    public long createObject(T obj) throws Exception {
        return baseDao.insertObject(obj);
    }

    @Override
    public int deleteObjectById(Serializable id) throws Exception {
        return baseDao.deleteObjectById(id);
    }

    @Override
    public int deleteObjectsByName(String name) throws Exception {
        return baseDao.deleteObjectsByName(name);
    }

    @Override
    public int updateObject(T obj) throws Exception {
        return baseDao.updateObject(obj);
    }

    @Override
    public List<T> findObjects() throws Exception {
        return baseDao.selectObjectsAll();
    }

    @Override
    public Map<String, Object> findObjects(Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsAll();
        return findObjectsMethod(data);
    }

    @Override
    public T findObject(Serializable id) throws Exception {
        return baseDao.selectObjectById(id);
    }

    @Override
    public Map<String, Object> findObjects(String name, Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsByName(name);
        return findObjectsMethod(data);
    }

    @Override
    public Map<String, Object> findObjects(T object, Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsByObject(object);
        return findObjectsMethod(data);
    }

    @Override
    public List<T> findObjectsForIdAndName() throws Exception {
        return baseDao.selectObjectsForIdAndName();
    }

    @Override
    public List<T> findObjects(T object) throws Exception {
        return baseDao.selectObjectsByObject(object);
    }

    @Override
    public List<T> findByNameAndAddress(String nameAddress, Serializable id, Long subdistrictId) throws Exception {
        return baseDao.selectByNameAndAddress(nameAddress, id, subdistrictId);
    }

    @Override
    public List<T> findByPhones(List<String> phones, Serializable id, Long subdistrictId) throws Exception {
        return baseDao.selectByPhones(phones, id, subdistrictId);
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
    Map<String, Object> findObjectsMethod(List<T> data) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("data", data);
        map.put("pageInfo", new PageInfo<T>(data));
        return map;
    }

    /**
     * 查找社区编号
     *
     * @param systemUser        系统用户对象
     * @param systemRoleId      系统角色编号
     * @param communityRoleId   社区角色编号
     * @param subdistrictRoleId 街道级角色编号
     * @param pageNumber        页面页码
     * @param pageDataSize      页面展示数据数量
     * @return 社区编号数组
     */
    List<Long> findCommunityIds(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Long roleId = systemUser.getRoleId();
        Long roleLocationId = systemUser.getRoleLocationId();
        List<Long> communityIds = new ArrayList<>();
        if (roleId.equals(communityRoleId)) {
            communityIds.add(roleLocationId);
        } else if (roleId.equals(subdistrictRoleId)) {
            List<Community> communities = communitiesDao.selectCommunitiesCorrelationBySubdistrictId(roleLocationId);
            for (Community community : communities) {
                communityIds.add(community.getCommunityId());
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
        return CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(cell, CellType.STRING))));
    }

    /**
     * 转换单元格数字类型
     *
     * @param cell 需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    Integer convertCell(Cell cell) {
        return Integer.parseInt(String.valueOf(ExcelUtil.getCellValue(cell, CellType.NUMERIC)));
    }

    /**
     * 分包人处理
     *
     * @param subcontractorName      分包人姓名
     * @param subcontractorTelephone 分包人联系方式
     * @param subcontractors         从数据库查询的分包人集合对象
     * @param communityId            所属社区编号
     * @return 分包人编号
     */
    Long addSubcontractorHandler(String subcontractorName, String subcontractorTelephone, List<Subcontractor> subcontractors, Long communityId) {
        Long subcontractorId = null;
        for (Subcontractor subcontractor : subcontractors) {
            if (subcontractorName.equals(subcontractor.getName())) {
                subcontractorId = subcontractor.getSubcontractorId();
                if (StringUtils.isEmpty(subcontractor.getTelephone())) {
                    subcontractor.setTelephone(subcontractorTelephone);
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    TransactionStatus status = transactionManager.getTransaction(def);
                    subcontractorsDao.updateObject(subcontractor);
                    transactionManager.commit(status);
                }
                break;
            }
        }
        if (subcontractorId == null) {
            Subcontractor newSubcontractor = new Subcontractor();
            newSubcontractor.setName(subcontractorName);
            newSubcontractor.setTelephone(subcontractorTelephone);
            newSubcontractor.setCommunityId(communityId);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            subcontractorsDao.insertObject(newSubcontractor);
            transactionManager.commit(status);
            subcontractors.add(newSubcontractor);
        }
        return subcontractorId;
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
            communityId = communitiesDao.selectCommunityIdByCommunityName(communityName);
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
    void setCommunityVariables(Long subdistrictId) {
        subcontractors = subcontractorsDao.selectObjectsAll();
        List<Community> communities = communitiesDao.selectCommunitiesBySubdistrictId(subdistrictId);
        communityMap = new HashMap<>(communities.size() + 1);
    }

    /**
     * 柱状图数据处理
     *
     * @param label        饼图图例
     * @param companyLabel 柱状图横坐标文字
     * @param object       饼图数据内容
     * @return 处理后的对象
     */
    Map<String, Object> barChartDataHandler(String label, String companyLabel, LinkedList<Map<String, Object>> object) {
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
        return data;
    }

    /**
     * 获取单位类型和单位编号
     *
     * @param systemUser    登录的系统用户对象
     * @param companyId     查找的范围单位的编号
     * @param companyRoleId 查找的范围单位的类别编号
     * @param pageNumber    分页页码
     * @param pageDataSize  每页展示的数量
     * @return 单位类型和单位编号集合
     */
    Map<String, Long> getCompany(SystemUser systemUser, Long companyId, Long companyRoleId, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        if (companyRoleId == null || companyId == null) {
            companyRoleId = systemUser.getRoleId();
            companyId = systemUser.getRoleLocationId();
        }
        Map<String, Long> company = new HashMap<>(3);
        company.put("companyId", companyId);
        company.put("companyRoleId", companyRoleId);
        PageHelper.startPage(pageNumber, pageDataSize);
        return company;
    }
}
