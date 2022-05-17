package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.*;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@Slf4j
@Service
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager>
    implements DormitoryManagerService {
    private static final String DATA_COLUMN_NAME = "人数";
    private final CompanyMapper companyMapper;
    private final SystemUserMapper systemUserMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final DormitoryManagerPhoneNumberMapper dormitoryManagerPhoneNumberMapper;
    private Integer excelDormitoryCommunityNameCellNumber;
    private Integer excelDormitoryNameCellNumber;
    private Integer excelDormitoryBirthCellNumber;
    private Integer excelDormitoryPoliticalStatusCellNumber;
    private Integer excelDormitoryEmploymentStatusCellNumber;
    private Integer excelDormitoryEducationCellNumber;
    private Integer excelDormitoryAddressCellNumber;
    private Integer excelDormitoryManagerAddressCellNumber;
    private Integer excelDormitoryManagerCountCellNumber;
    private Integer excelDormitoryTelephoneCellNumber;
    private Integer excelDormitoryLandlineCellNumber;
    private Integer excelDormitorySubcontractorTelephoneCellNumber;

    @Autowired
    public DormitoryManagerServiceImpl(CompanyMapper companyMapper, SystemUserMapper systemUserMapper,
        PhoneNumberMapper phoneNumberMapper, DormitoryManagerPhoneNumberMapper dormitoryManagerPhoneNumberMapper) {
        this.companyMapper = companyMapper;
        this.systemUserMapper = systemUserMapper;
        this.phoneNumberMapper = phoneNumberMapper;
        this.dormitoryManagerPhoneNumberMapper = dormitoryManagerPhoneNumberMapper;
    }

    @Override
    public DormitoryManager getCorrelation(Long id) {
        List<String> info = new ArrayList<>();
        DormitoryManager dormitoryManager = baseMapper.selectAndCompanyById(id);
        info.add(dormitoryManager.getCompany().getName());
        info.add(dormitoryManager.getSystemUser().getUsername());
        dormitoryManager.setSubcontractorInfo(info);
        return dormitoryManager;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Map<String, JSONObject> configurationMap) {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbers = new ArrayList<>();
        getUploadExcelVariable(configurationMap);
        List<Company> companies = companyMapper.selectList(null);
        List<SystemUser> systemUsers = systemUserMapper.selectAndPhoneNumber();
        List<Long> phoneNumberIds = dormitoryManagerPhoneNumberMapper.selectList(null).stream()
            .map(DormitoryManagerPhoneNumber::getPhoneNumberId).collect(Collectors.toList());
        List<PhoneNumber> phoneNumberAll = phoneNumberMapper.selectList(null);
        for (List<Object> datum : data) {
            DormitoryManager dormitoryManager = new DormitoryManager();
            String companyName = StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryCommunityNameCellNumber)));
            Optional<Company> company = companies.stream().filter(c -> c.getName().contains(companyName)).findFirst();
            if (company.isEmpty()) {
                throw new BusinessException("单位读取失败！");
            }
            String systemUserPhoneNumber =
                StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitorySubcontractorTelephoneCellNumber)));
            Optional<SystemUser> systemUser = systemUsers.stream()
                .filter(user -> user.getPhoneNumber().getPhoneNumber().equals(systemUserPhoneNumber)).findFirst();
            if (systemUser.isEmpty()) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            String idNumber = StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryBirthCellNumber)));
            dormitoryManager.setId(IdWorker.getId()).setCompanyId(company.get().getId())
                .setName(StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryNameCellNumber))))
                .setIdNumber(idNumber).setGender(GenderEnum.getEnum(IdcardUtil.getGenderByIdCard(idNumber)))
                .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(idNumber), IdcardUtil.getMonthByIdCard(idNumber),
                    IdcardUtil.getDayByIdCard(idNumber)))
                .setPoliticalStatus(PoliticalStatusEnum
                    .getEnum(StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryPoliticalStatusCellNumber)))))
                .setEmploymentStatus(EmploymentStatusEnum
                    .getEnum(StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryEmploymentStatusCellNumber)))))
                .setEducation(EducationStatusEnum
                    .getEnum(StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryEducationCellNumber)))))
                .setAddress(StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryAddressCellNumber))))
                .setManagerAddress(
                    StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryManagerAddressCellNumber))))
                .setManagerCount(Convert.toInt(datum.get(excelDormitoryManagerCountCellNumber)))
                .setSystemUserId(systemUser.get().getId());
            String telephone = StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryTelephoneCellNumber)));
            String landline = StrUtil.cleanBlank(String.valueOf(datum.get(excelDormitoryLandlineCellNumber)));
            if (StrUtil.isNotEmpty(telephone)) {
                PhoneNumber tel = new PhoneNumber();
                tel.setId(IdWorker.getId()).setPhoneNumber(telephone).setPhoneType(PhoneTypeEnum.MOBILE);
                checkPhoneNumberId(phoneNumberIds, phoneNumberAll, telephone, tel);
                phoneNumbers.add(tel);
                dormitoryManagerPhoneNumbers.add(new DormitoryManagerPhoneNumber()
                    .setDormitoryManagerId(dormitoryManager.getId()).setPhoneNumberId(tel.getId()));
            }
            if (StrUtil.isNotEmpty(landline)) {
                PhoneNumber land = new PhoneNumber();
                land.setId(IdWorker.getId()).setPhoneNumber(landline).setPhoneType(PhoneTypeEnum.FIXED_LINE);
                checkPhoneNumberId(phoneNumberIds, phoneNumberAll, landline, land);
                phoneNumbers.add(land);
                dormitoryManagerPhoneNumbers.add(new DormitoryManagerPhoneNumber()
                    .setDormitoryManagerId(dormitoryManager.getId()).setPhoneNumberId(land.getId()));
            }
            dormitoryManagers.add(dormitoryManager);
        }
        if (dormitoryManagers.size() == 0) {
            throw new BusinessException("上传失败！");
        }
        phoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbers);
        baseMapper.insertBatchSomeColumn(dormitoryManagers);
        return dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbers) > 0;
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(SystemUser currentSystemUser,
        Map<String, JSONObject> configurationMap) {
        String excelDormitoryTitleUp = Convert.toStr(configurationMap.get("excel_dormitory_title_up").get("content"));
        String excelDormitoryTitle = Convert.toStr(configurationMap.get("excel_dormitory_title").get("content"));
        List<Company> companyAll = companyMapper.selectList(null);
        List<Long> subordinateCompanyIds =
            exportExcelGetSubordinateCompanyIds(currentSystemUser, configurationMap, companyAll);
        List<DormitoryManager> dormitoryManagers = baseMapper.selectListByCompanyIds(subordinateCompanyIds);
        if (dormitoryManagers.isEmpty()) {
            return null;
        }
        List<LinkedHashMap<String, Object>> list = exportData(companyAll, dormitoryManagers);
        ExcelWriter excelWriter = ExcelUtil.getBigWriter();
        CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
        setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
        excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, 1, excelDormitoryTitleUp,
            firstRowStyle);
        exportExcelTitleHandle(excelWriter, excelDormitoryTitle, list.get(0).keySet().size());
        StyleSet styleSet = excelWriter.getStyleSet();
        setCellStyle(styleSet.getHeadCellStyle(), excelWriter, "宋体", (short)11, false, true, true);
        setCellStyle(styleSet.getCellStyle(), excelWriter, "宋体", (short)9, false, true, true);
        Map<String, String> tableHead = getTableHead();
        int index = 0;
        for (String value : tableHead.values()) {
            if (index == tableHead.size() - 2) {
                excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), index, index + 1, "分包人",
                    true);
                excelWriter.writeCellValue(index, excelWriter.getCurrentRow() + 1, value);
            } else if (index == tableHead.size() - 1) {
                excelWriter.writeCellValue(index, excelWriter.getCurrentRow() + 1, value);
            } else {
                excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow() + 1, index, index, value,
                    true);
            }
            index++;
        }
        excelWriter.passCurrentRow();
        excelWriter.passCurrentRow();
        excelWriter.write(list, false);
        for (int i = 0; i < tableHead.size(); ++i) {
            excelWriter.autoSizeColumn(i);
        }
        return excelWriter;
    }

    @Override
    public IPage<DormitoryManager> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        return baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize), search, sort);
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long[] companyIds) {
        List<Company> companyAll = companyMapper.selectList(null);
        if (companies == null) {
            companies = companyAll.stream().filter(company -> company.getParentId() == 0L).collect(Collectors.toList());
        }
        if (companyIds == null) {
            companyIds = companies.stream().map(Company::getId).toArray(Long[]::new);
        }
        return computedBaseMessage(companyAll, companyIds);
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, Boolean typeParam) {
        String label = "社区楼长分包总户数";
        List<Company> companyAll = companyMapper.selectList(null);
        LinkedList<Map<String, Object>> dormitoryManager = new LinkedList<>();
        if (companyIds == null) {
            // CommonUtil.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
        }
        return null;// barChartDataHandler(label, null, "户", dormitoryManager);
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
        entity.setGender(GenderEnum.getEnum(IdcardUtil.getGenderByIdCard(entity.getIdNumber())))
            .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(entity.getIdNumber()),
                IdcardUtil.getMonthByIdCard(entity.getIdNumber()), IdcardUtil.getDayByIdCard(entity.getIdNumber())))
            .setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSystemUserId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        baseMapper.insert(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        return dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(DormitoryManager entity) {
        entity.setGender(GenderEnum.getEnum(IdcardUtil.getGenderByIdCard(entity.getIdNumber())))
            .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(entity.getIdNumber()),
                IdcardUtil.getMonthByIdCard(entity.getIdNumber()), IdcardUtil.getDayByIdCard(entity.getIdNumber())))
            .setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSystemUserId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        if (phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers()) > 0) {
            dormitoryManagerPhoneNumberMapper
                .delete(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", entity.getId()));
            dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity));
        }
        return isSuccess;
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
     * @param companyAll
     *            所有单位集合
     * @param companyIds
     *            单位编号集合
     * @return 录入统计信息
     */
    private Map<String, Object> computedBaseMessage(List<Company> companyAll, Long[] companyIds) {
        Map<String, Object> baseMessage = new HashMap<>(5);
        int maleCount = 0;
        int femaleCount = 0;
        int unknownCount = 0;
        int[] ageCounts = {0, 0, 0, 0, 0, 0, 0, 0};
        Map<String, Integer> educationMap = Arrays.stream(EducationStatusEnum.values())
            .collect(Collectors.toMap(EducationStatusEnum::getDescription, value -> 0));
        Map<String, Integer> politicalStatusMap = Arrays.stream(PoliticalStatusEnum.values())
            .collect(Collectors.toMap(PoliticalStatusEnum::getDescription, value -> 0));
        Map<String, Integer> employmentStatusMap = Arrays.stream(EmploymentStatusEnum.values())
            .collect(Collectors.toMap(EmploymentStatusEnum::getDescription, value -> 0));
        LambdaQueryWrapper<DormitoryManager> wrapper = new LambdaQueryWrapper<>();
        Arrays.stream(companyIds).forEach(companyId -> {
            List<Long> recursionCompanyIds = CommonUtil.listRecursionCompanyIds(companyAll, companyId);
            if (!recursionCompanyIds.isEmpty()) {
                recursionCompanyIds.forEach(id -> wrapper.eq(DormitoryManager::getCompanyId, id).or());
            } else {
                wrapper.eq(DormitoryManager::getCompanyId, companyId).or();
            }
        });
        List<DormitoryManager> dormitoryManagers = baseMapper.selectList(wrapper);
        LocalDate now = LocalDate.now();
        for (DormitoryManager dormitoryManager : dormitoryManagers) {
            switch (dormitoryManager.getGender()) {
                case MALE:
                    maleCount += 1;
                    break;
                case FEMALE:
                    femaleCount += 1;
                    break;
                default:
                    unknownCount += 1;
                    break;
            }
            long age = dormitoryManager.getBirth().until(now, ChronoUnit.YEARS);
            if (age < 20L) {
                ageCounts[0] += 1;
            } else if (age < 30L) {
                ageCounts[1] += 1;
            } else if (age < 40L) {
                ageCounts[2] += 1;
            } else if (age < 50L) {
                ageCounts[3] += 1;
            } else if (age < 60L) {
                ageCounts[4] += 1;
            } else if (age < 70L) {
                ageCounts[5] += 1;
            } else if (age < 80L) {
                ageCounts[6] += 1;
            } else {
                ageCounts[7] += 1;
            }
            educationMap.put(dormitoryManager.getEducation().getDescription(),
                educationMap.get(dormitoryManager.getEducation().getDescription()) + 1);
            politicalStatusMap.put(dormitoryManager.getPoliticalStatus().getDescription(),
                politicalStatusMap.get(dormitoryManager.getPoliticalStatus().getDescription()) + 1);
            employmentStatusMap.put(dormitoryManager.getEmploymentStatus().getDescription(),
                employmentStatusMap.get(dormitoryManager.getEmploymentStatus().getDescription()) + 1);
        }
        baseMessage.put("inputCount", dormitoryManagers.size());
        baseMessage.put("genderCount", genderCount(maleCount, femaleCount, unknownCount));
        baseMessage.put("ageCount", ageCount(ageCounts));
        baseMessage.put("educationCount", mapCount("educationType", educationMap));
        baseMessage.put("politicalStatusCount", mapCount("politicalStatusType", politicalStatusMap));
        baseMessage.put("employmentStatusCount", mapCount("employmentStatusType", employmentStatusMap));
        return baseMessage;
    }

    /**
     * 生成map统计
     *
     * @param mapType
     *            Map类型
     * @param map
     *            定义Map
     * @return 统计对象
     */
    private Map<String, Object> mapCount(String mapType, Map<String, Integer> map) {
        Map<String, Object> count = new HashMap<>(3);
        String value = "value";
        List<Map<String, Object>> countData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Map<String, Object> temp = new HashMap<>(2);
            temp.put(mapType, entry.getKey());
            temp.put(value, entry.getValue());
            countData.add(temp);
        }
        count.put("data", countData);
        count.put("xField", value);
        count.put("yField", mapType);
        return count;
    }

    /**
     * 生成年龄统计
     *
     * @param ageCounts
     *            年龄统计数组
     * @return 统计对象
     */
    private Map<String, Object> ageCount(int[] ageCounts) {
        Map<String, Object> ageCount = new HashMap<>(3);
        String value = "value";
        String ageType = "ageType";
        List<Map<String, Object>> ageCountData = new ArrayList<>();
        Map<String, Object> ageCountDataTemp = new HashMap<>(8);
        ageCountDataTemp.put("20岁以下", ageCounts[0]);
        ageCountDataTemp.put("20岁~29岁", ageCounts[1]);
        ageCountDataTemp.put("30岁~39岁", ageCounts[2]);
        ageCountDataTemp.put("40岁~49岁", ageCounts[3]);
        ageCountDataTemp.put("50岁~59岁", ageCounts[4]);
        ageCountDataTemp.put("60岁~69岁", ageCounts[5]);
        ageCountDataTemp.put("70岁~79岁", ageCounts[6]);
        ageCountDataTemp.put("80岁以上", ageCounts[7]);
        for (Map.Entry<String, Object> entry : ageCountDataTemp.entrySet()) {
            Map<String, Object> tempMap = new HashMap<>(2);
            tempMap.put(ageType, entry.getKey());
            tempMap.put(value, entry.getValue());
            ageCountData.add(tempMap);
        }
        ageCount.put("data", ageCountData);
        ageCount.put("xField", value);
        ageCount.put("yField", ageType);
        return ageCount;
    }

    /**
     * 生成性别统计
     *
     * @param maleCount
     *            男性数量
     * @param femaleCount
     *            女性数量
     * @param unknownCount
     *            未知数量
     * @return 统计对象
     */
    private Map<String, Object> genderCount(int maleCount, int femaleCount, int unknownCount) {
        Map<String, Object> genderCount = new HashMap<>(5);
        List<Map<String, Object>> genderCountData = new ArrayList<>();
        String value = "value";
        String genderType = "genderType";
        Map<String, Object> genderCountDataMaleMap = new HashMap<>(2);
        genderCountDataMaleMap.put(genderType, GenderEnum.MALE.getDescription());
        genderCountDataMaleMap.put(value, maleCount);
        Map<String, Object> genderCountDataFemaleMap = new HashMap<>(2);
        genderCountDataFemaleMap.put(genderType, GenderEnum.FEMALE.getDescription());
        genderCountDataFemaleMap.put(value, femaleCount);
        Map<String, Object> genderCountDataUnknownMap = new HashMap<>(2);
        genderCountDataUnknownMap.put(genderType, GenderEnum.UNKNOWN.getDescription());
        genderCountDataUnknownMap.put(value, unknownCount);
        genderCountData.add(genderCountDataMaleMap);
        genderCountData.add(genderCountDataFemaleMap);
        genderCountData.add(genderCountDataUnknownMap);
        genderCount.put("data", genderCountData);
        genderCount.put("xField", value);
        genderCount.put("yField", genderType);
        return genderCount;
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
        tableHead.put("idNumber", "身份证号码");
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

    /**
     * 获取上传Excel的变量
     *
     * @param configurationMap
     *            配置项
     */
    private void getUploadExcelVariable(Map<String, JSONObject> configurationMap) {
        excelDormitoryCommunityNameCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_community_name_cell_number").get("content"));
        excelDormitoryNameCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_name_cell_number").get("content"));
        excelDormitoryBirthCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_birth_cell_number").get("content"));
        excelDormitoryPoliticalStatusCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_political_status_cell_number").get("content"));
        excelDormitoryEmploymentStatusCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_employment_status_cell_number").get("content"));
        excelDormitoryEducationCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_education_cell_number").get("content"));
        excelDormitoryAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_address_cell_number").get("content"));
        excelDormitoryManagerAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_manager_address_cell_number").get("content"));
        excelDormitoryManagerCountCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_manager_count_cell_number").get("content"));
        excelDormitoryTelephoneCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_telephone_cell_number").get("content"));
        excelDormitoryLandlineCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_landline_cell_number").get("content"));
        excelDormitorySubcontractorTelephoneCellNumber =
            Convert.toInt(configurationMap.get("excel_dormitory_subcontractor_telephone_cell_number").get("content"));
    }

    /**
     * 导出数据库数据
     *
     * @param companyAll
     *            所有单位集合
     * @param dormitoryManagers
     *            需要导出的数据集合
     * @return 数据集合
     */
    private List<LinkedHashMap<String, Object>> exportData(List<Company> companyAll,
        List<DormitoryManager> dormitoryManagers) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        long index = 1L;
        List<SystemUser> systemUsers = systemUserMapper.selectAndPhoneNumber();
        String streetTitle = "";
        for (DormitoryManager dormitoryManager : dormitoryManagers) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            // 序号
            data.put("sequenceNumber", index);
            // 处理社区名称
            String communityName =
                dormitoryManager.getCompany().getName().replaceAll(SystemConstant.COMMUNITY_NAME_PATTERN, "");
            data.put("communityName", communityName);
            Optional<Company> company = companyAll.stream()
                .filter(c -> dormitoryManager.getCompany().getParentId().equals(c.getId())).findFirst();
            String streetName = "";
            if (company.isPresent()) {
                streetName = company.get().getName().replaceAll(SystemConstant.STREET_NAME_PATTERN, "");
                if (StrUtil.isBlankIfStr(streetTitle)) {
                    streetTitle = company.get().getName();
                } else if (!streetTitle.equals(company.get().getName())) {
                    streetTitle = streetTitle + "，" + company.get().getName();
                }
            }
            // 编号
            String companyFirstLetter = PinyinUtil.getFirstLetter(streetName, "").toUpperCase()
                + PinyinUtil.getFirstLetter(communityName, "").toUpperCase();
            data.put("id", companyFirstLetter + String.format("%04d", index));
            // 姓名
            data.put("name", dormitoryManager.getName());
            // 性别
            data.put("genderName", dormitoryManager.getGender().getDescription());
            data.put("idNumber", dormitoryManager.getIdNumber());
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
                .filter(systemUser -> dormitoryManager.getSystemUser().getId().equals(systemUser.getId())).findFirst();
            if (user.isPresent()) {
                data.put("subcontractorTelephone", user.get().getPhoneNumber().getPhoneNumber());
            } else {
                data.put("subcontractorTelephone", "");
            }
            list.add(data);
            index++;
        }
        return list;
    }

    /**
     * 检测联系方式编号
     *
     * @param phoneNumberIds
     *            中间联系方式编号集合
     * @param phoneNumberAll
     *            所有联系方式集合
     * @param number
     *            联系方式
     * @param phoneNumber
     *            输出联系方式对象
     */
    private void checkPhoneNumberId(List<Long> phoneNumberIds, List<PhoneNumber> phoneNumberAll, String number,
        PhoneNumber phoneNumber) {
        Optional<PhoneNumber> numberOptional =
            phoneNumberAll.stream().filter(num -> number.equals(num.getPhoneNumber())).findFirst();
        if (numberOptional.isPresent()) {
            if (phoneNumberIds.contains(numberOptional.get().getId())) {
                throw new BusinessException("导入数据时有重复数据：" + numberOptional.get().getPhoneNumber() + "，请检查后再次导入！");
            }
            phoneNumber.setId(numberOptional.get().getId());
        }
    }
}
