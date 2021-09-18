package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.*;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityMapper;
import com.github.phonenumbermanager.mapper.DormitoryManagerMapper;
import com.github.phonenumbermanager.mapper.SubcontractorMapper;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.promeg.pinyinhelper.Pinyin;

import cn.hutool.core.convert.Convert;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@Service("dormitoryManagerService")
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager>
    implements DormitoryManagerService {
    private static final String DATA_COLUMN_NAME = "人数";
    private final Pattern idPatten = Pattern.compile("(?iUs)([A-Za-z]+)(\\d+)");
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private SubcontractorMapper subcontractorMapper;
    @Resource
    private PhoneNumberService phoneNumberService;

    @Override
    public DormitoryManager getCorrelation(Serializable id) {
        return baseMapper.selectAndCommunityById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap)
        throws ParseException {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        Integer excelDormitoryCommunityNameCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_community_name_cell_number"));
        Integer excelDormitoryIdCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_id_cell_number"));
        Integer excelDormitoryNameCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_name_cell_number"));
        Integer excelDormitoryGenderCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_gender_cell_number"));
        Integer excelDormitoryBirthCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_birth_cell_number"));
        Integer excelDormitoryPoliticalStatusCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_political_status_cell_number"));
        Integer excelDormitoryEmploymentStatusCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_employment_status_cell_number"));
        Integer excelDormitoryEducationCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_education_cell_number"));
        Integer excelDormitoryAddressCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_address_cell_number"));
        Integer excelDormitoryManagerAddressCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_manager_address_cell_number"));
        Integer excelDormitoryManagerCountCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_manager_count_cell_number"));
        Integer excelDormitoryTelephoneCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_telephone_cell_number"));
        Integer excelDormitoryLandlineCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_landline_cell_number"));
        Integer excelDormitorySubcontractorNameCellNumber =
            Convert.toInt(configurationsMap.get("excel_dormitory_subcontractor_name_cell_number"));
        List<Community> communities = communityMapper.selectBySubdistrictId(subdistrictId);
        List<Subcontractor> subcontractors = subcontractorMapper.selectBySubdistrictId(subdistrictId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        for (List<Object> datum : data) {
            DormitoryManager dormitoryManager = new DormitoryManager();
            List<Community> communityList = communities.stream()
                .filter(community -> community.getName()
                    .contains(String.valueOf(datum.get(excelDormitoryCommunityNameCellNumber))))
                .collect(Collectors.toList());
            if (communityList.size() == 0) {
                throw new BusinessException("未找到对应的社区，请重试！");
            }
            Date birth = simpleDateFormat.parse(String.valueOf(datum.get(excelDormitoryBirthCellNumber)));
            dormitoryManager.setCommunityId(communityList.get(0).getId())
                .setId(String.valueOf(datum.get(excelDormitoryIdCellNumber)))
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
                tel.setPhoneNumber(telephone).setPhoneType(PhoneTypeEnum.MOBILE).setSourceId(dormitoryManager.getId())
                    .setSourceType(PhoneNumberSourceTypeEnum.DORMITORY_MANAGER);
                phoneNumbers.add(tel);
            }
            if (StringUtils.isNotEmpty(landline)) {
                PhoneNumber land = new PhoneNumber();
                land.setPhoneNumber(landline).setPhoneType(PhoneTypeEnum.LANDLINE).setSourceId(dormitoryManager.getId())
                    .setSourceType(PhoneNumberSourceTypeEnum.DORMITORY_MANAGER);
                phoneNumbers.add(land);
            }
            List<Subcontractor> subcontractorList = subcontractors.stream()
                .filter(subcontractor -> subcontractor.getName()
                    .equals(String.valueOf(datum.get(excelDormitorySubcontractorNameCellNumber))))
                .collect(Collectors.toList());
            if (subcontractorList.size() == 0) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            dormitoryManager.setSubcontractorId(subcontractorList.get(0).getId());
            dormitoryManagers.add(dormitoryManager);
        }
        if (dormitoryManagers.size() > 0) {
            QueryWrapper<DormitoryManager> wrapper = new QueryWrapper<>();
            wrapper.eq("subdistrict_id", subdistrictId);
            baseMapper.delete(wrapper);
            phoneNumberService.saveBatch(phoneNumbers);
            return saveBatch(dormitoryManagers);
        }
        return false;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getCorrelation(Serializable communityCompanyType,
        Serializable subdistrictCompanyType, List<Map<String, Object>> userData) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        List<DormitoryManager> dormitoryManagers =
            baseMapper.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        if (dormitoryManagers != null) {
            long index = 1L;
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                // 序号
                data.put("sequenceNumber", index);
                // 处理社区名称
                String communityName = dormitoryManager.getCommunity().getName()
                    .replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                data.put("communityName", communityName);
                data.put("id", dormitoryManager.getId());
                data.put("genderName", dormitoryManager.getGender().getDescription());
                // 出生年月
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
                data.put("birthString", dateFormat.format(dormitoryManager.getBirth()));
                data.put("politicalStatusName", dormitoryManager.getPoliticalStatus().getDescription());
                data.put("educationName", dormitoryManager.getEmploymentStatus().getDescription());
                data.put("address", dormitoryManager.getAddress());
                data.put("managerAddress", dormitoryManager.getManagerAddress());
                data.put("managerCount", dormitoryManager.getManagerCount());
                data.put("telephone", dormitoryManager.getPhoneNumbers().get(0));
                // 处理分包人
                data.put("subcontractorName", dormitoryManager.getSubcontractor().getName());
                data.put("subcontractorTelephone", JSON.toJSON(dormitoryManager.getSubcontractor().getPhoneNumbers()));
                list.add(data);
                index++;
            }
        }
        return list;
    }

    @Override
    public IPage<DormitoryManager> get(SystemUser systemUser, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType, DormitoryManager dormitoryManager,
        Serializable companyId, Serializable companyRoleId, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyRoleId);
        Page<DormitoryManager> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectByUserRole(page, (Serializable)company.get("companyType"),
            (Serializable)company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType,
            dormitoryManager);
    }

    @Override
    public String get(Serializable communityId, String communityName, String subdistrictName) {
        StringBuilder lastId = new StringBuilder();
        String id = baseMapper.selectLastIdByCommunityId(communityId);
        long idNumber = 0L;
        if (StringUtils.isNotEmpty(id)) {
            Matcher matcher = idPatten.matcher(id);
            while (matcher.find()) {
                lastId.append(matcher.group(1).toUpperCase());
                idNumber = Long.parseLong(matcher.group(2));
            }
        } else {
            String sn = subdistrictName.replaceAll("(?iUs)[街道办事处]", "");
            String cn = communityName.replaceAll("(?iUs)[社区居委会]", "");
            String regex = ",";
            String subdistrictNamePinyin = Pinyin.toPinyin(sn, regex);
            String communityNamePinyin = Pinyin.toPinyin(cn, regex);
            String[] subdistrictNamePinyinSplit = subdistrictNamePinyin.split(regex);
            String[] communityNamePinyinSplit = communityNamePinyin.split(regex);
            for (String split : subdistrictNamePinyinSplit) {
                lastId.append(split.toUpperCase(), 0, 1);
            }
            for (String split : communityNamePinyinSplit) {
                lastId.append(split.toUpperCase(), 0, 1);
            }
        }
        lastId.append(String.format("%04d", idNumber + 1));
        return lastId.toString();
    }

    @Override
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Map<String, Long> genderCount;
        Map<String, Long> ageData = null;
        Map<String, Long> educationData = null;
        Map<String, Long> politicalStatusData = null;
        Map<String, Long> workStatusData = null;
        long ct = (long)(companyType == null ? 0L : companyType);
        boolean isSystemRole = companyType == null || companyId == null || ct == (int)systemCompanyType;
        if (isSystemRole) {
            genderCount = baseMapper.countGenderAll();
            ageData = baseMapper.countAgeRangeAll();
            educationData = baseMapper.countEducationRangeAll();
            politicalStatusData = baseMapper.countPoliticalStatusRangeAll();
            workStatusData = baseMapper.countWorkStatusRangeAll();
        } else if (ct == (int)communityCompanyType) {
            genderCount = baseMapper.countGenderByCommunityId(companyId);
            ageData = baseMapper.countAgeRangeCommunityId(companyId);
            educationData = baseMapper.countEducationRangeCommunityId(companyId);
            politicalStatusData = baseMapper.countPoliticalStatusRangeCommunityId(companyId);
            workStatusData = baseMapper.countWorkStatusRangeCommunityId(companyId);
        } else if (ct == (int)subdistrictCompanyType) {
            genderCount = baseMapper.countGenderBySubdistrictId(companyId);
            ageData = baseMapper.countAgeRangeSubdistrictId(companyId);
            educationData = baseMapper.countEducationRangeSubdistrictId(companyId);
            politicalStatusData = baseMapper.countPoliticalStatusRangeSubdistrictId(companyId);
            workStatusData = baseMapper.countWorkStatusRangeSubdistrictId(companyId);
        } else {
            genderCount = new HashMap<>(3);
            genderCount.put("male", 0L);
            genderCount.put("female", 0L);
        }
        baseMessage.put("gender", genderCount);
        if (ageData != null) {
            Map<String, Object> agePieData = getPieData(ageData, "年龄范围");
            baseMessage.put("age", agePieData);
        }
        if (educationData != null) {
            Map<String, Object> educationPieData = getPieData(educationData, "教育程度");
            baseMessage.put("education", educationPieData);
        }
        if (politicalStatusData != null) {
            Map<String, Object> politicalStatusPieData = getPieData(politicalStatusData, "政治面貌");
            baseMessage.put("politicalStatus", politicalStatusPieData);
        }
        if (workStatusData != null) {
            Map<String, Object> workStatusPieData = getPieData(workStatusData, "工作状况");
            baseMessage.put("workStatus", workStatusPieData);
        }
        return baseMessage;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Boolean typeParam, Serializable systemCompanyType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType) {
        String label = "社区楼长分包总户数";
        String companyLabel = null;
        LinkedList<Map<String, Object>> dormitoryManager;
        long ct = (long)(companyType == null ? 0L : companyType);
        // TODO: 2021/9/12 0012 用户权限
        // boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getLevel().getValue())
        // || (systemCompanyType.equals(systemUser.getLevel().getValue()) && ct == (int)subdistrictCompanyType);
        if (companyType == null || ct == (int)systemCompanyType) {
            companyLabel = "街道";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = baseMapper.countForGroupSubdistrict();
            } else {
                dormitoryManager = baseMapper.sumManagerCountForGroupSubdistrict();
            }
            // } else if (isSystemRoleCount) {
            // companyLabel = "社区";
            // if (typeParam) {
            // label = "社区楼长总人数";
            // dormitoryManager = baseMapper.countForGroupCommunity(companyId);
            // } else {
            // dormitoryManager = baseMapper.sumManagerCountForGroupCommunity(companyId);
            // }
            // } else if ((int)communityCompanyType == systemUser.getLevel().getValue()) {
            // companyLabel = "社区";
            // if (typeParam) {
            // label = "社区楼长总人数";
            // dormitoryManager = baseMapper.countForGroupByCommunityId(companyId);
            // } else {
            // dormitoryManager = baseMapper.sumManagerCountForGroupByCommunityId(companyId);
            // }
        } else {
            dormitoryManager = new LinkedList<>();
        }
        return barChartDataHandler(label, companyLabel, "户", dormitoryManager);
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
    private Map<String, Object> getPieData(Map<String, Long> data, String column) {
        Map<String, Object> pieData = new HashMap<>(3);
        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>(3);
            row.put(column, entry.getKey());
            row.put(DATA_COLUMN_NAME, entry.getValue());
            rows.add(row);
        }
        pieData.put("columns", new String[] {column, DATA_COLUMN_NAME});
        pieData.put("rows", rows);
        return pieData;
    }
}
