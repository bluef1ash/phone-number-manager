package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.enums.*;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.AllArgsConstructor;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager>
    implements DormitoryManagerService {
    private static final String DATA_COLUMN_NAME = "人数";
    private final CompanyMapper companyMapper;
    private final SystemUserMapper systemUserMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final DormitoryManagerPhoneNumberMapper dormitoryManagerPhoneNumberMapper;

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
        List<Company> companies = companyMapper.selectList(new QueryWrapper<Company>().eq("parent_id", streetId));
        List<SystemUser> systemUsers = systemUserMapper.selectAndPhoneNumber();
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
            phoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbers);
            return saveBatch(dormitoryManagers);
        }
        throw new BusinessException("上传失败！");
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(List<Company> companies, Map<String, JSONObject> configurationMap) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        String excelDormitoryTitleUp = Convert.toStr(configurationMap.get("excel_dormitory_title_up").get("content"));
        String excelDormitoryTitle = Convert.toStr(configurationMap.get("excel_dormitory_title").get("content"));
        String streetTitle = "";
        List<Company> companyAll = companyMapper.selectList(null);
        List<SystemUser> systemUsers = systemUserMapper.selectAndPhoneNumber();
        if (companies == null) {
            companies = companyAll;
        }
        Company levelMax =
            companyMapper.selectOne(new QueryWrapper<Company>().orderByDesc("level").select("level").last("limit 1"));
        List<Long> companyIds = new ArrayList<>();
        CommonUtil.listSubmissionCompanyIds(companyIds, companies, companyAll, levelMax.getLevel(), null);
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        int current = 1;
        while (true) {
            IPage<DormitoryManager> dormitoryManagerPage =
                baseMapper.selectListByCompanyIds(companyIds, new Page<>(current, 1000));
            if (dormitoryManagerPage.getRecords().isEmpty()) {
                break;
            }
            dormitoryManagers.addAll(dormitoryManagerPage.getRecords());
            current++;
        }
        if (!dormitoryManagers.isEmpty()) {
            long index = 1L;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM");
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                // 序号
                data.put("sequenceNumber", index);
                // 处理社区名称
                String communityName = dormitoryManager.getCompany().getName().replaceAll("(?iUs)[社区居委会]", "");
                data.put("communityName", communityName);
                Optional<Company> company = companyAll.stream()
                    .filter(c -> dormitoryManager.getCompany().getParentId().equals(c.getId())).findFirst();
                String streetName = "";
                if (company.isPresent()) {
                    streetName = company.get().getName().replaceAll("(?iUs)[街道办事处]", "");
                    if (StrUtil.isBlankIfStr(streetTitle)) {
                        streetTitle = company.get().getName();
                    } else if (!streetTitle.equals(company.get().getName())) {
                        streetTitle = streetTitle + "，" + company.get().getName();
                    }
                }
                // 编号
                data.put("id", PinyinUtil.getFirstLetter(streetName, "") + PinyinUtil.getFirstLetter(communityName, "")
                    + String.format("%04d", index));
                // 姓名
                data.put("name", dormitoryManager.getName());
                // 性别
                data.put("genderName", dormitoryManager.getGender().getDescription());
                // 出生年月
                data.put("birthString", dateTimeFormatter.format(dormitoryManager.getBirth()));
                data.put("politicalStatusName", dormitoryManager.getPoliticalStatus().getDescription());
                data.put("employmentStatusName", dormitoryManager.getEmploymentStatus().getDescription());
                data.put("educationName", dormitoryManager.getEducation().getDescription());
                data.put("address", dormitoryManager.getAddress());
                data.put("managerAddress", dormitoryManager.getManagerAddress());
                data.put("managerCount", dormitoryManager.getManagerCount());
                data.put("mobile", "");
                data.put("fixedLine", "");
                dormitoryManager.getPhoneNumbers().forEach(phoneNumber -> {
                    if (PhoneUtil.isMobile(phoneNumber.getPhoneNumber())) {
                        data.put("mobile", phoneNumber.getPhoneNumber());
                    } else {
                        data.put("fixedLine", phoneNumber.getPhoneNumber());
                    }
                });
                // 处理分包人
                data.put("subcontractorName", dormitoryManager.getSystemUser().getUsername());
                Optional<SystemUser> user = systemUsers.stream()
                    .filter(systemUser -> dormitoryManager.getSystemUser().getId().equals(systemUser.getId()))
                    .findFirst();
                if (user.isPresent()) {
                    data.put("subcontractorTelephone", user.get().getPhoneNumber().getPhoneNumber());
                } else {
                    data.put("subcontractorTelephone", "");
                }
                list.add(data);
                index++;
            }
            ExcelWriter excelWriter = ExcelUtil.getWriter(true);
            CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, 1, excelDormitoryTitleUp,
                firstRowStyle);
            excelWriter.passCurrentRow();
            CellStyle titleStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(titleStyle, excelWriter, "方正小标宋简体", (short)16, false, false, false);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, list.get(0).keySet().size(),
                excelDormitoryTitle, titleStyle);
            excelWriter.passCurrentRow();
            StyleSet styleSet = excelWriter.getStyleSet();
            setCellStyle(styleSet.getHeadCellStyle(), excelWriter, "宋体", (short)11, false, true, true);
            setCellStyle(styleSet.getCellStyle(), excelWriter, "宋体", (short)9, false, true, true);
            Map<String, String> tableHead = getTableHead();
            int i = 0;
            for (String value : tableHead.values()) {
                if (i == tableHead.size() - 2) {
                    excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), i, i + 1, "分包人", true);
                    excelWriter.writeCellValue(i, excelWriter.getCurrentRow() + 1, value);
                } else if (i == tableHead.size() - 1) {
                    excelWriter.writeCellValue(i, excelWriter.getCurrentRow() + 1, value);
                } else {
                    excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow() + 1, i, i, value, true);
                }
                i++;
            }
            excelWriter.passCurrentRow();
            excelWriter.passCurrentRow();
            excelWriter.write(list, false);
            excelWriter.autoSizeColumnAll();
            return excelWriter;
        }
        return null;
    }

    @Override
    public IPage<DormitoryManager> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        return baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize), search, sort);
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        List<Company> companyAll = companyMapper.selectList(null);
        List<Long> companyIds = new ArrayList<>();
        if (companyId != null) {
            CommonUtil.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
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
        List<Company> companyAll = companyMapper.selectList(null);
        LinkedList<Map<String, Object>> dormitoryManager = new LinkedList<>();
        List<Long> companyIds = new ArrayList<>();
        if (companyId == null) {
            CommonUtil.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
        }
        return barChartDataHandler(label, null, "户", dormitoryManager);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        return dormitoryManagerPhoneNumberMapper
            .delete(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", id)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(DormitoryManager entity) {
        baseMapper.insert(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        return dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(DormitoryManager entity) {
        super.updateById(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        dormitoryManagerPhoneNumberMapper
            .delete(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", entity.getId()));
        return dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<?> list) {
        QueryWrapper<DormitoryManagerPhoneNumber> wrapper = new QueryWrapper<>();
        list.forEach(l -> wrapper.eq("dormitory_manager_id", l).or());
        baseMapper.deleteBatchIds(list);
        return dormitoryManagerPhoneNumberMapper.delete(wrapper) > 0;
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
        Company levelMax = companyMapper.selectOne(new QueryWrapper<Company>().orderByDesc("level").select("level"));
        CommonUtil.listSubmissionCompanyIds(companyIds, companies, companyAll, levelMax.getLevel(), null);
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

    /**
     * 处理社区居民楼长联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbersHandler(DormitoryManager entity) {
        QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> wrapper.eq("phone_number", phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(phoneNumber -> new DormitoryManagerPhoneNumber()
            .setDormitoryManagerId(entity.getId()).setPhoneNumberId(phoneNumber.getId())).collect(Collectors.toList());
    }

    /**
     * 获取表格表头
     *
     * @return 表格表头
     */
    private Map<String, String> getTableHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("sequenceNumber", "序号");
        tableHead.put("communityName", "社区名称");
        tableHead.put("id", "编号");
        tableHead.put("name", "姓名");
        tableHead.put("genderName", "性别");
        tableHead.put("birthString", "出生年月");
        tableHead.put("politicalStatusName", "政治面貌");
        tableHead.put("employmentStatusName", "工作状况");
        tableHead.put("educationName", "文化程度");
        tableHead.put("address", "家庭住址（具体到单元号、楼号）");
        tableHead.put("managerAddress", "分包楼栋（具体到单元号、楼号）");
        tableHead.put("managerCount", "联系户数");
        tableHead.put("mobile", "手机");
        tableHead.put("fixedLine", "座机");
        tableHead.put("subcontractorName", "姓名");
        tableHead.put("subcontractorTelephone", "手机");
        return tableHead;
    }
}
