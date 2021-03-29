package com.github.phonenumbermanager.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.ExcelUtil;
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
import java.util.*;

/**
 * 基础业务实现
 *
 * @param <M> <T> SERVICE泛型
 * @author 廿二月的天
 */
public abstract class BaseServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    private CommonMapper<T> commonMapper;
    @Resource
    protected SystemUserMapper systemUserMapper;
    @Resource
    protected DormitoryManagerMapper dormitoryManagerMapper;
    @Resource
    protected UserRoleMapper userRoleMapper;
    @Resource
    protected UserPrivilegeMapper userPrivilegeMapper;
    @Resource
    protected UserRolePrivilegeMapper userRolePrivilegeMapper;
    @Resource
    protected SubdistrictMapper subdistrictMapper;
    @Resource
    protected CommunityMapper communityMapper;
    @Resource
    protected CommunityResidentMapper communityResidentMapper;
    @Resource
    protected ConfigurationMapper configurationMapper;
    @Resource
    protected SubcontractorMapper subcontractorMapper;
    @Resource
    protected PhoneNumberMapper phoneNumberMapper;
    List<Subcontractor> subcontractors;
    Map<String, Long> communityMap;


    /**
     * 最先运行的方法，自动加载对应类型的Mapper
     *
     * @throws NoSuchFieldException   找不到参数异常
     * @throws IllegalAccessException 错误转换异常
     */
    @PostConstruct
    private void initBaseMapper() throws NoSuchFieldException, IllegalAccessException {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        String localField = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
        Field field = this.getClass().getSuperclass().getDeclaredField(localField);
        Field baseField = this.getClass().getSuperclass().getDeclaredField("commonMapper");
        baseField.set(this, field.get(this));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String name) {
        return commonMapper.deleteByName(name) > 0;
    }

    @Override
    public List<T> get(T object) {
        return commonMapper.selectByObject(object);
    }

    @Override
    public List<T> get(String nameAddress, Serializable id, Serializable subdistrictId) {
        return commonMapper.selectByNameAndAddress(nameAddress, id, subdistrictId);
    }

    @Override
    public List<T> get(List<PhoneNumber> phoneNumbers, Serializable id, Serializable subdistrictId, PhoneNumberSourceTypeEnum sourceType) {
        return commonMapper.selectByPhones(phoneNumbers, id, subdistrictId, sourceType);
    }

    @Override
    public Map<String, String> getPartStatHead() {
        return null;
    }

    @Override
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public IPage<T> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        return null;
    }

    @Override
    public boolean save(Workbook workbook, Serializable subdistrictId, Map<String, Object> configurationsMap) {
        return false;
    }

    @Override
    public IPage<T> getCorrelation(SystemUser systemUser, PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum, Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber, Integer pageDataSize) {
        List<Serializable> companyIds = getCommunityIds(systemUser, communityCompanyType, subdistrictCompanyType);
        Page<T> page = new Page<>(pageNumber, pageDataSize);
        return commonMapper.selectAndCommunityByCommunityIds(page, companyIds, phoneNumberSourceTypeEnum);
    }

    @Override
    public T getCorrelation(Serializable id) {
        return null;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    /**
     * 查找社区编号
     *
     * @param systemUser             系统用户对象
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 社区编号数组
     */
    List<Serializable> getCommunityIds(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Integer level = systemUser.getLevel().getValue();
        Long companyId = systemUser.getCompanyId();
        List<Serializable> communityIds = new ArrayList<>();
        if (level.equals(communityCompanyType)) {
            communityIds.add(companyId);
        } else if (level.equals(subdistrictCompanyType)) {
            List<Community> communities = communityMapper.selectCorrelationBySubdistrictId(companyId);
            for (Community community : communities) {
                communityIds.add(community.getId());
            }
        } else {
            communityIds = null;
        }
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
     * @param name           分包人姓名
     * @param mobile         分包人联系方式
     * @param subcontractors 从数据库查询的分包人集合对象
     * @param communityId    所属社区编号
     * @return 分包人编号
     */
    @Transactional(rollbackFor = Exception.class)
    Long addSubcontractorHandler(String name, String mobile, List<Subcontractor> subcontractors, Serializable communityId) {
        Long id = null;
        for (Subcontractor subcontractor : subcontractors) {
            if (name.equals(subcontractor.getName())) {
                id = subcontractor.getId();
                if (StringUtils.isEmpty(subcontractor.getPhoneNumbers().get(0).getPhoneNumber())) {
                    PhoneNumber phoneNumber = phoneNumberMapper.selectBySourceTypeAndSourceId(PhoneNumberSourceTypeEnum.SUBCONTRACTOR, subcontractor.getId());
                    phoneNumber.setPhoneNumber(mobile);
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    subcontractorMapper.updateById(subcontractor);
                    phoneNumberMapper.updateById(phoneNumber);
                }
                break;
            }
        }
        if (id == null) {
            Subcontractor newSubcontractor = new Subcontractor();
            newSubcontractor.setName(name);
            newSubcontractor.setCommunityId((Long) communityId);
            List<String> numbers = new ArrayList<>();
            numbers.add(mobile);
            List<PhoneNumber> phoneNumbers = CommonUtil.setPhoneNumbers(numbers);
            newSubcontractor.setPhoneNumbers(phoneNumbers);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            subcontractorMapper.insert(newSubcontractor);
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
            communityId = (Long) communityMapper.selectIdByName(communityName);
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
        subcontractors = subcontractorMapper.selectList(null);
        List<Community> communities = communityMapper.selectBySubdistrictId(subdistrictId);
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
     * @param systemUser  登录的系统用户对象
     * @param companyId   查找的范围单位的编号
     * @param companyType 查找的范围单位的类别编号
     * @return 单位类型和单位编号集合
     */
    Map<String, Object> getCompany(SystemUser systemUser, Serializable companyId, Serializable companyType) {
        if (companyType == null || companyId == null) {
            companyType = systemUser.getLevel();
            companyId = systemUser.getCompanyId();
        }
        Map<String, Object> company = new HashMap<>(3);
        company.put("companyId", companyId);
        company.put("companyType", companyType);
        return company;
    }
}
