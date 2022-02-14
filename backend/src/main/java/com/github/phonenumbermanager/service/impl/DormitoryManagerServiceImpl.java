package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.*;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.DormitoryManagerMapper;
import com.github.phonenumbermanager.service.*;
import com.github.phonenumbermanager.vo.DormitoryManagerSearchVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@Service("dormitoryManagerService")
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager>
    implements DormitoryManagerService {
    private static final String DATA_COLUMN_NAME = "人数";
    @Resource
    private CompanyService companyService;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private DormitoryManagerPhoneNumberService dormitoryManagerPhoneNumberService;

    @Override
    public DormitoryManager getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Long streetId, Map<String, JSONObject> configurationMap) {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        Integer excelDormitoryCommunityNameCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_community_name_cell_number").get("content"));
        Integer excelDormitoryNameCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_name_cell_number").get("content"));
        Integer excelDormitoryGenderCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_gender_cell_number").get("content"));
        Integer excelDormitoryBirthCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_birth_cell_number").get("content"));
        Integer excelDormitoryPoliticalStatusCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_political_status_cell_number").get("content"));
        Integer excelDormitoryEmploymentStatusCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_employment_status_cell_number").get("content"));
        Integer excelDormitoryEducationCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_education_cell_number").get("content"));
        Integer excelDormitoryAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_address_cell_number").get("content"));
        Integer excelDormitoryManagerAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_manager_address_cell_number").get("content"));
        Integer excelDormitoryManagerCountCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_manager_count_cell_number").get("content"));
        Integer excelDormitoryTelephoneCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_telephone_cell_number").get("content"));
        Integer excelDormitoryLandlineCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_landline_cell_number").get("content"));
        Integer excelDormitorySubcontractorTelephoneCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_subcontractor_telephone_cell_number").get("content"));
        List<Company> companies = companyService.list(new QueryWrapper<Company>().eq("parent_id", streetId));
        List<SystemUser> systemUsers = systemUserService.listCorrelationPhoneNumber();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM");
        for (List<Object> datum : data) {
            DormitoryManager dormitoryManager = new DormitoryManager();
            Optional<Company> company = companies.stream()
                .filter(c -> c.getName().contains(String.valueOf(datum.get(excelDormitoryCommunityNameCellNumber))))
                .findFirst();
            if (company.isEmpty()) {
                throw new BusinessException("单位读取失败！");
            }
            LocalDate birth =
                LocalDate.parse(String.valueOf(datum.get(excelDormitoryBirthCellNumber)), dateTimeFormatter);
            dormitoryManager.setCompanyId(company.get().getId())
                .setName(String.valueOf(datum.get(excelDormitoryNameCellNumber)))
                .setGender(GenderEnum.valueOf(String.valueOf(datum.get(excelDormitoryGenderCellNumber))))
                .setBirth(birth)
                .setPoliticalStatus(
                    PoliticalStatusEnum.valueOf(String.valueOf(datum.get(excelDormitoryPoliticalStatusCellNumber))))
                .setEmploymentStatus(
                    EmploymentStatusEnum.valueOf(String.valueOf(datum.get(excelDormitoryEmploymentStatusCellNumber))))
                .setEducation(EducationStatusEnum.valueOf(String.valueOf(datum.get(excelDormitoryEducationCellNumber))))
                .setAddress(String.valueOf(datum.get(excelDormitoryAddressCellNumber)))
                .setManagerAddress(String.valueOf(datum.get(excelDormitoryManagerAddressCellNumber)))
                .setManagerCount((Integer)datum.get(excelDormitoryManagerCountCellNumber));
            String telephone = String.valueOf(datum.get(excelDormitoryTelephoneCellNumber));
            String landline = String.valueOf(datum.get(excelDormitoryLandlineCellNumber));
            if (StringUtils.isNotEmpty(telephone)) {
                PhoneNumber tel = new PhoneNumber();
                tel.setPhoneNumber(telephone).setPhoneType(PhoneTypeEnum.MOBILE);
                phoneNumbers.add(tel);
            }
            if (StringUtils.isNotEmpty(landline)) {
                PhoneNumber land = new PhoneNumber();
                land.setPhoneNumber(landline).setPhoneType(PhoneTypeEnum.FIXED_LINE);
                phoneNumbers.add(land);
            }
            Optional<
                SystemUser> user =
                    systemUsers.stream()
                        .filter(systemUser -> systemUser.getPhoneNumber().getPhoneNumber()
                            .equals(String.valueOf(datum.get(excelDormitorySubcontractorTelephoneCellNumber))))
                        .findFirst();
            if (user.isEmpty()) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            dormitoryManager.setSystemUserId(user.get().getId());
            dormitoryManagers.add(dormitoryManager);
        }
        if (dormitoryManagers.size() > 0) {
            QueryWrapper<DormitoryManager> wrapper = new QueryWrapper<>();
            companies.forEach(company -> wrapper.eq("company_id", company.getId()).or());
            baseMapper.delete(wrapper.or());
            phoneNumberService.saveBatch(phoneNumbers);
            return saveBatch(dormitoryManagers);
        }
        throw new BusinessException("上传失败！");
    }

    @Override
    public List<LinkedHashMap<String, Object>> listCorrelationToMap(Long companyId) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        List<Company> companies = companyService.list(new QueryWrapper<Company>().eq("parent_id", companyId));
        List<Long> companyIds = new ArrayList<>();
        companyService.listSubmissionCompanyIds(companyIds, companies, companyService.list(), null);
        List<DormitoryManager> dormitoryManagers = baseMapper.selectListByCompanyIds(companyIds);
        if (!dormitoryManagers.isEmpty()) {
            long index = 1L;
            QueryWrapper<Company> wrapper = new QueryWrapper<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM");
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                // 序号
                data.put("sequenceNumber", index);
                // 处理社区名称
                String communityName = dormitoryManager.getCompany().getName()
                    .replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                data.put("communityName", communityName);
                wrapper.eq("id", dormitoryManager.getCompany().getParentId());
                Company company = companyService.getOne(wrapper);
                String streetName = company.getName().replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "")
                    .replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                data.put("id", PinyinUtil.getFirstLetter(streetName, "") + PinyinUtil.getFirstLetter(communityName, "")
                    + String.format("%04d", index));
                data.put("genderName", dormitoryManager.getGender().getDescription());
                // 出生年月
                data.put("birthString", dateTimeFormatter.format(dormitoryManager.getBirth()));
                data.put("politicalStatusName", dormitoryManager.getPoliticalStatus().getDescription());
                data.put("educationName", dormitoryManager.getEmploymentStatus().getDescription());
                data.put("address", dormitoryManager.getAddress());
                data.put("managerAddress", dormitoryManager.getManagerAddress());
                data.put("managerCount", dormitoryManager.getManagerCount());
                data.put("telephone", dormitoryManager.getPhoneNumbers().get(0));
                // 处理分包人
                data.put("subcontractorName", dormitoryManager.getSystemUser().getUsername());
                SystemUser systemUser = systemUserService.getCorrelation(dormitoryManager.getSystemUser().getId());
                data.put("subcontractorTelephone", systemUser.getPhoneNumber());
                list.add(data);
                index++;
            }
        }
        return list;
    }

    @Override
    public IPage<DormitoryManager> page(List<Company> companies, DormitoryManagerSearchVo dormitoryManagerSearchVo,
        IPage<DormitoryManager> page) {
        // TODO: 2021/10/19 0019 SQL需要测试
        return baseMapper.selectBySearchVo(companies, dormitoryManagerSearchVo, page);
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        List<Company> companyAll = companyService.list();
        List<Long> companyIds = new ArrayList<>();
        if (companyId != null) {
            companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
        }
        Map<String, Map<String, Integer>> computedBaseMessage = computedBaseMessage(companies, companyAll, companyIds);
        Map<String, Object> baseMessage = new HashMap<>();
        baseMessage.put("gender", computedBaseMessage.get("genderCount"));
        if (computedBaseMessage.get("ageCount") != null) {
            Map<String, Object> agePieData = getPieData(computedBaseMessage.get("ageCount"), "年龄范围");
            baseMessage.put("age", agePieData);
        }
        if (computedBaseMessage.get("educationCount") != null) {
            Map<String, Object> educationPieData = getPieData(computedBaseMessage.get("educationCount"), "教育程度");
            baseMessage.put("education", educationPieData);
        }
        if (computedBaseMessage.get("politicalStatusCount") != null) {
            Map<String, Object> politicalStatusPieData =
                getPieData(computedBaseMessage.get("politicalStatusCount"), "政治面貌");
            baseMessage.put("politicalStatus", politicalStatusPieData);
        }
        if (computedBaseMessage.get("employmentStatusCount") != null) {
            Map<String, Object> employmentStatusPieData =
                getPieData(computedBaseMessage.get("employmentStatusCount"), "工作状况");
            baseMessage.put("employmentStatusCount", employmentStatusPieData);
        }
        return baseMessage;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long companyId, Boolean typeParam) {
        String label = "社区楼长分包总户数";
        List<Company> companyAll = companyService.list();
        LinkedList<Map<String, Object>> dormitoryManager = new LinkedList<>();
        List<Long> companyIds = new ArrayList<>();
        if (companyId == null) {
            companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
        }
        return barChartDataHandler(label, null, "户", dormitoryManager);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0 && dormitoryManagerPhoneNumberService
            .remove(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(DormitoryManager entity) {
        baseMapper.insert(entity);
        // TODO: 2021/10/23 0023 测试mybatis plus 重复插入
        /* phoneNumberService.list();
        entity.getPhoneNumbers()
            .forEach(phoneNumber -> dormitoryManagerPhoneNumbers.add(new DormitoryManagerPhoneNumber()
                .setDormitoryManagerId(entity.getId()).setPhoneNumberId(phoneNumber.getId())));*/
        List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbers = new ArrayList<>();
        return dormitoryManagerPhoneNumberService.saveBatch(dormitoryManagerPhoneNumbers);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(DormitoryManager entity) {
        baseMapper.updateById(entity);
        // TODO: 2021/10/23 0023 测试是否有冗余数据
        phoneNumberService.updateBatchById(entity.getPhoneNumbers());
        List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbers = new ArrayList<>();
        entity.getPhoneNumbers()
            .forEach(phoneNumber -> dormitoryManagerPhoneNumbers.add(new DormitoryManagerPhoneNumber()
                .setDormitoryManagerId(entity.getId()).setPhoneNumberId(phoneNumber.getId())));
        return dormitoryManagerPhoneNumberService.updateBatchById(dormitoryManagerPhoneNumbers);
    }

    /**
     * 获取饼图数据
     *
     * @param data
     *            需要设置的数据
     * @param column
     *            饼图的图例
     * @return 饼图需要的数据
     */
    private Map<String, Object> getPieData(Map<String, Integer> data, String column) {
        Map<String, Object> pieData = new HashMap<>(3);
        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>(3);
            row.put(column, entry.getKey());
            row.put(DATA_COLUMN_NAME, entry.getValue());
            rows.add(row);
        }
        pieData.put("columns", new String[] {column, DATA_COLUMN_NAME});
        pieData.put("rows", rows);
        return pieData;
    }

    /**
     * 计算录入统计信息
     *
     * @param companies
     *            单位集合
     * @param companyAll
     *            所有单位集合
     * @param companyIds
     *            单位编号集合
     * @return 录入统计信息
     */
    private Map<String, Map<String, Integer>> computedBaseMessage(List<Company> companies, List<Company> companyAll,
        List<Long> companyIds) {
        Map<String, Map<String, Integer>> baseMessage = new HashMap<>(5);
        Map<String, Integer> genderCount = new HashMap<>(2);
        genderCount.put("男性", 0);
        genderCount.put("女性", 0);
        Map<String, Integer> ageCount = new HashMap<>(8);
        ageCount.put("20岁以下", 0);
        ageCount.put("20岁~29岁", 0);
        ageCount.put("30岁~39岁", 0);
        ageCount.put("40岁~49岁", 0);
        ageCount.put("50岁~59岁", 0);
        ageCount.put("60岁~69岁", 0);
        ageCount.put("70岁~79岁", 0);
        ageCount.put("80岁以上", 0);
        Map<String, Integer> educationCount = new HashMap<>();
        Arrays.stream(EducationStatusEnum.values())
            .forEach(educationStatusEnum -> educationCount.put(educationStatusEnum.getDescription(), 0));
        Map<String, Integer> politicalStatusCount = new HashMap<>();
        Arrays.stream(PoliticalStatusEnum.values())
            .forEach(politicalStatusEnum -> politicalStatusCount.put(politicalStatusEnum.getDescription(), 0));
        Map<String, Integer> employmentStatusCount = new HashMap<>();
        Arrays.stream(EmploymentStatusEnum.values())
            .forEach(employmentStatusEnum -> employmentStatusCount.put(employmentStatusEnum.getDescription(), 0));
        companyService.listSubmissionCompanyIds(companyIds, companies, companyAll, null);
        QueryWrapper<DormitoryManager> wrapper = new QueryWrapper<>();
        companyIds.forEach(id -> wrapper.eq("company_id", id));
        List<DormitoryManager> dormitoryManagers = baseMapper.selectList(wrapper);
        LocalDate now = LocalDate.now();
        for (DormitoryManager dormitoryManager : dormitoryManagers) {
            switch (dormitoryManager.getGender()) {
                case MALE:
                    genderCount.put("男性", genderCount.get("男性") + 1);
                    break;
                case FEMALE:
                    genderCount.put("女性", genderCount.get("女性") + 1);
                    break;
                default:
                    genderCount.put("未知", genderCount.get("未知") + 1);
                    break;
            }
            int age = dormitoryManager.getBirth().until(now).getYears();
            if (age < 20) {
                ageCount.put("20岁以下", ageCount.get("20岁以下") + 1);
            } else if (age < 30) {
                ageCount.put("20岁~29岁", ageCount.get("20岁~29岁") + 1);
            } else if (age < 40) {
                ageCount.put("30岁~39岁", ageCount.get("30岁~39岁") + 1);
            } else if (age < 50) {
                ageCount.put("40岁~49岁", ageCount.get("40岁~49岁") + 1);
            } else if (age < 60) {
                ageCount.put("50岁~59岁", ageCount.get("50岁~59岁") + 1);
            } else if (age < 70) {
                ageCount.put("60岁~69岁", ageCount.get("60岁~69岁") + 1);
            } else if (age < 80) {
                ageCount.put("70岁~79岁", ageCount.get("70岁~79岁") + 1);
            } else {
                ageCount.put("80岁以上", ageCount.get("80岁以上") + 1);
            }
            educationCount.put(dormitoryManager.getEducation().getDescription(),
                educationCount.get(dormitoryManager.getEducation().getDescription()) + 1);
            politicalStatusCount.put(dormitoryManager.getPoliticalStatus().getDescription(),
                politicalStatusCount.get(dormitoryManager.getPoliticalStatus().getDescription()) + 1);
            employmentStatusCount.put(dormitoryManager.getEmploymentStatus().getDescription(),
                employmentStatusCount.get(dormitoryManager.getEmploymentStatus().getDescription()) + 1);
        }
        baseMessage.put("genderCount", genderCount);
        baseMessage.put("ageCount", ageCount);
        baseMessage.put("educationCount", educationCount);
        baseMessage.put("politicalStatusCount", politicalStatusCount);
        baseMessage.put("employmentStatusCount", employmentStatusCount);
        return baseMessage;
    }
}
