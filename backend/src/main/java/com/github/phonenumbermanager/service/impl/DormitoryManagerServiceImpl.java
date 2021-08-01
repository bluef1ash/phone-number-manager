package com.github.phonenumbermanager.service.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.*;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.DormitoryManagerMapper;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.ExcelUtil;
import com.github.promeg.pinyinhelper.Pinyin;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@Service("dormitoryManagerService")
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager> implements DormitoryManagerService {

    private static final String SUBDISTRICT_NAME_PLACEHOLDER = "${subdistrictName}";
    private static final String SUBDISTRICT_NAME_REGEX = "(?Us)[${]{2}subdistrictName[}]";
    private static final String DATA_COLUMN_NAME = "人数";
    private Integer excelDormitoryCommunityNameCellNumber;
    private Integer excelDormitoryIdCellNumber;
    private Integer excelDormitoryNameCellNumber;
    private Integer excelDormitoryGenderCellNumber;
    private Integer excelDormitoryBirthCellNumber;
    private Integer excelDormitoryPoliticalStatusCellNumber;
    private Integer excelDormitoryWorkStatusCellNumber;
    private Integer excelDormitoryEducationCellNumber;
    private Integer excelDormitoryAddressCellNumber;
    private Integer excelDormitoryManagerAddressCellNumber;
    private Integer excelDormitoryManagerCountCellNumber;
    private Integer excelDormitoryTelephoneCellNumber;
    private Integer excelDormitoryLandlineCellNumber;
    private Integer excelDormitorySubcontractorNameCellNumber;
    private Integer excelDormitorySubcontractorTelephoneCellNumber;
    private String fileTitle = "XX";
    private String[] titles;
    private final Pattern idPatten = Pattern.compile("(?iUs)([A-Za-z]+)(\\d+)");
    /*private final ExcelUtil.DataHandler excelDataHandler = (params, headers) -> {
        Integer rowIndex = (Integer) params.get("rowIndex");
        Workbook workbook = (Workbook) params.get("workbook");
        Sheet sheet = (Sheet) params.get("sheet");
        // 附件格式
        if (StringUtils.isNotEmpty(titles[0])) {
            CellStyle attachStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 12, false, false, false);
            Row attachTitle = sheet.createRow(rowIndex);
            attachTitle.createCell(0).setCellValue(titles[0]);
            attachTitle.getCell(0).setCellStyle(attachStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
            sheet.getRow(rowIndex).setHeight((short) (15.6 * 20));
            rowIndex++;
        }
        // 表头
        Row titleRow = sheet.createRow(rowIndex);
        CellStyle titleStyle = ExcelUtil.setCellStyle(workbook, "方正小标宋简体", (short) 18, false, false, false);
        titleRow.createCell(0).setCellValue(fileTitle);
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, getPartStatHead().size() - 1));
        sheet.getRow(rowIndex).setHeight((short) (48 * 20));
        rowIndex++;
        CellStyle dateStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 11, false, false, false);
        // 单位、日期
        Row dateRow = sheet.createRow(rowIndex);
        int unitCellIndex = 10;
        int dateCellIndex = 12;
        dateRow.createCell(unitCellIndex).setCellValue("单位：户");
        dateRow.getCell(unitCellIndex).setCellStyle(dateStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, unitCellIndex, unitCellIndex + 1));
        dateRow.createCell(dateCellIndex).setCellValue("时间：" + DateUtil.date2Str("yyyy年MM月dd日", new Date()));
        dateRow.getCell(dateCellIndex).setCellStyle(dateStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, dateCellIndex, dateCellIndex + 3));
        sheet.getRow(rowIndex).setHeight((short) (26.1 * 20));
        rowIndex++;
        // 列头
        Row headerRow = sheet.createRow(rowIndex);
        CellStyle headerStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 11, false, true, false);
        Row subcontractorRow = sheet.createRow(rowIndex + 1);
        int subcontractorStartCellIndex = headers.length - 2;
        for (int i = 0; i < headers.length; i++) {
            if (i < subcontractorStartCellIndex) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + 1, i, i));
                subcontractorRow.createCell(i).setCellStyle(headerStyle);
            } else {
                if (i == subcontractorStartCellIndex) {
                    headerRow.createCell(i + 1).setCellStyle(headerStyle);
                    headerRow.createCell(i).setCellValue("分包社区工作者");
                    headerRow.getCell(i).setCellStyle(headerStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, i, i + 1));
                }
                subcontractorRow.createCell(i).setCellValue(headers[i]);
                subcontractorRow.getCell(i).setCellStyle(headerStyle);
            }
        }
        sheet.setAutoFilter(new CellRangeAddress(rowIndex, rowIndex, 0, headers.length - 1));
        CellStyle contentStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 9, false, true, true);
        rowIndex += 2;
        sheet.createFreezePane(0, rowIndex);
        params.put("rowIndex", rowIndex);
        params.put("workbook", workbook);
        params.put("sheet", sheet);
        params.put("contentStyle", contentStyle);
    };*/

    @Override
    public DormitoryManager getCorrelation(Serializable id) {
        return dormitoryManagerMapper.selectAndCommunityById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap) {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        excelDormitoryCommunityNameCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_community_name_cell_number"));
        excelDormitoryIdCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_id_cell_number"));
        excelDormitoryNameCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_name_cell_number"));
        excelDormitoryGenderCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_gender_cell_number"));
        excelDormitoryBirthCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_birth_cell_number"));
        excelDormitoryPoliticalStatusCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_political_status_cell_number"));
        excelDormitoryWorkStatusCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_work_status_cell_number"));
        excelDormitoryEducationCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_education_cell_number"));
        excelDormitoryAddressCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_address_cell_number"));
        excelDormitoryManagerAddressCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_manager_address_cell_number"));
        excelDormitoryManagerCountCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_manager_count_cell_number"));
        excelDormitoryTelephoneCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_telephone_cell_number"));
        excelDormitoryLandlineCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_landline_cell_number"));
        excelDormitorySubcontractorNameCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_subcontractor_name_cell_number"));
        excelDormitorySubcontractorTelephoneCellNumber = Convert.toInt(configurationsMap.get("excel_dormitory_subcontractor_telephone_cell_number"));
        setCommunityVariables(subdistrictId);
        for (List<Object> column : data) {

        }
        if (dormitoryManagers.size() > 0) {
            QueryWrapper<DormitoryManager> wrapper = new QueryWrapper<>();
            wrapper.eq("subdistrict_id", subdistrictId);
            return saveBatch(dormitoryManagers);
        }
        return false;
    }

    @Override
    public Map<String, String> getPartStatHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("sequenceNumber", "序号");
        tableHead.put("communityName", "社区名称");
        tableHead.put("id", "编号");
        tableHead.put("name", "姓名");
        tableHead.put("genderName", "性别");
        tableHead.put("birthString", "出生年月");
        tableHead.put("politicalStatusName", "政治面貌");
        tableHead.put("workStatusName", "工作状况");
        tableHead.put("educationName", "文化程度");
        tableHead.put("address", "家庭住址（具体到单元号、楼号）");
        tableHead.put("managerAddress", "分包楼栋（具体到单元号、楼号）");
        tableHead.put("managerCount", "联系户数");
        tableHead.put("telephone", "手机");
        tableHead.put("landline", "座机");
        tableHead.put("subcontractorName", "姓名");
        tableHead.put("subcontractorTelephone", "手机");
        return tableHead;
    }

    @Override
    public JSONArray getCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData, String[] titles) {
        this.titles = titles;
        List<DormitoryManager> dormitoryManagers = dormitoryManagerMapper.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        JSONArray jsonArray = new JSONArray();
        if (dormitoryManagers != null) {
            long index = 1L;
            Subdistrict subdistrict = subdistrictMapper.selectById(dormitoryManagers.get(0).getCommunity().getSubdistrictId());
            if (titles[1].contains(SUBDISTRICT_NAME_PLACEHOLDER) && StringUtils.isNotEmpty(subdistrict.getName())) {
                String subdistrictName = subdistrict.getName();
                fileTitle = titles[1].replaceAll(SUBDISTRICT_NAME_REGEX, subdistrictName.replaceAll("(?iUs)[街道办事处]", ""));
            }
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                JSONObject jsonObject = new JSONObject();
                // 序号
                jsonObject.put("sequenceNumber", index);
                // 处理社区名称
                String communityName = dormitoryManager.getCommunity().getName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                jsonObject.put("communityName", communityName);
                // 出生年月
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
                jsonObject.put("birth", dateFormat.format(dormitoryManager.getBirth()));
                // 处理分包人
                jsonObject.put("subcontractorName", dormitoryManager.getSubcontractor().getName());
                jsonObject.put("subcontractorTelephone", JSON.toJSON(dormitoryManager.getSubcontractor().getPhoneNumbers()));
                jsonArray.add(jsonObject);
                index++;
            }
        }
        return jsonArray;
    }

    @Override
    public IPage<DormitoryManager> get(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, DormitoryManager dormitoryManager, Serializable companyId, Serializable companyRoleId, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyRoleId);
        Page<DormitoryManager> page = new Page<>(pageNumber, pageDataSize);
        return dormitoryManagerMapper.selectByUserRole(page, (Serializable) company.get("companyType"), (Serializable) company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType, dormitoryManager);
    }

    @Override
    public String get(Serializable communityId, String communityName, String subdistrictName) {
        StringBuilder lastId = new StringBuilder();
        String id = dormitoryManagerMapper.selectLastIdByCommunityId(communityId);
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
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Map<String, Long> genderCount;
        Map<String, Long> ageData = null;
        Map<String, Long> educationData = null;
        Map<String, Long> politicalStatusData = null;
        Map<String, Long> workStatusData = null;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRole = companyType == null || companyId == null || ct == (int) systemCompanyType;
        if (isSystemRole) {
            genderCount = dormitoryManagerMapper.countGenderAll();
            ageData = dormitoryManagerMapper.countAgeRangeAll();
            educationData = dormitoryManagerMapper.countEducationRangeAll();
            politicalStatusData = dormitoryManagerMapper.countPoliticalStatusRangeAll();
            workStatusData = dormitoryManagerMapper.countWorkStatusRangeAll();
        } else if (ct == (int) communityCompanyType) {
            genderCount = dormitoryManagerMapper.countGenderByCommunityId(companyId);
            ageData = dormitoryManagerMapper.countAgeRangeCommunityId(companyId);
            educationData = dormitoryManagerMapper.countEducationRangeCommunityId(companyId);
            politicalStatusData = dormitoryManagerMapper.countPoliticalStatusRangeCommunityId(companyId);
            workStatusData = dormitoryManagerMapper.countWorkStatusRangeCommunityId(companyId);
        } else if (ct == (int) subdistrictCompanyType) {
            genderCount = dormitoryManagerMapper.countGenderBySubdistrictId(companyId);
            ageData = dormitoryManagerMapper.countAgeRangeSubdistrictId(companyId);
            educationData = dormitoryManagerMapper.countEducationRangeSubdistrictId(companyId);
            politicalStatusData = dormitoryManagerMapper.countPoliticalStatusRangeSubdistrictId(companyId);
            workStatusData = dormitoryManagerMapper.countWorkStatusRangeSubdistrictId(companyId);
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
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType, Boolean typeParam, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String label = "社区楼长分包总户数";
        String companyLabel = null;
        LinkedList<Map<String, Object>> dormitoryManager;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getLevel().getValue()) || (systemCompanyType.equals(systemUser.getLevel().getValue()) && ct == (int) subdistrictCompanyType);
        if (companyType == null || ct == (int) systemCompanyType) {
            companyLabel = "街道";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerMapper.countForGroupSubdistrict();
            } else {
                dormitoryManager = dormitoryManagerMapper.sumManagerCountForGroupSubdistrict();
            }
        } else if (isSystemRoleCount) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerMapper.countForGroupCommunity(companyId);
            } else {
                dormitoryManager = dormitoryManagerMapper.sumManagerCountForGroupCommunity(companyId);
            }
        } else if ((int) communityCompanyType == systemUser.getLevel().getValue()) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerMapper.countForGroupByCommunityId(companyId);
            } else {
                dormitoryManager = dormitoryManagerMapper.sumManagerCountForGroupByCommunityId(companyId);
            }
        } else {
            dormitoryManager = new LinkedList<>();
        }
        return barChartDataHandler(label, companyLabel, "户", dormitoryManager);
    }

    @Override
    public String getFileTitle() {
        return fileTitle;
    }

    /**
     * 获取饼图数据
     *
     * @param data   需要设置的数据
     * @param column 饼图的图例
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
        pieData.put("columns", new String[]{column, DATA_COLUMN_NAME});
        pieData.put("rows", rows);
        return pieData;
    }

    /**
     * 导入Excel数据处理
     *
     * @param row Excel行对象
     * @return 处理完成的楼片长对象
     * @throws ParseException 日期转换异常
     */
    private DormitoryManager importExcelHandler(Row row) {
        DormitoryManager dormitoryManager = new DormitoryManager();
        String id = convertCellString(row.getCell(excelDormitoryIdCellNumber));
        dormitoryManager.setId(id);
        String communityName = convertCellString(row.getCell(excelDormitoryCommunityNameCellNumber));
        Long communityId = getCommunityId(communityMap, communityName);
        dormitoryManager.setCommunityId(communityId);
        dormitoryManager.setName(convertCellString(row.getCell(excelDormitoryNameCellNumber)));
        String genderName = convertCellString(row.getCell(excelDormitoryGenderCellNumber));
        if (GenderEnum.MALE.getDescription().equals(genderName)) {
            dormitoryManager.setGender(GenderEnum.MALE);
        } else if (GenderEnum.FEMALE.getDescription().equals(genderName)) {
            dormitoryManager.setGender(GenderEnum.FEMALE);
        } else {
            dormitoryManager.setGender(GenderEnum.UNKNOWN);
        }
        Cell birthCell = row.getCell(excelDormitoryBirthCellNumber);
        Date birth = null;
        if (ExcelUtil.DEFAULT_DATE_PATTERN_EN.equals(birthCell.getCellStyle().getDataFormatString())) {
            birth = birthCell.getDateCellValue();
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String birthString = convertCellString(birthCell).replaceAll("(?iUs)^(\\d{4})[.-/年]?(\\d{1,2})[.-/月]?(?:\\d{1,2})?[日]?$", "$1-$2");
            try {
                birth = dateFormat.parse(birthString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dormitoryManager.setBirth(birth);
        String political = convertCellString(row.getCell(excelDormitoryPoliticalStatusCellNumber));
        for (PoliticalStatusEnum politicalStatusEnum : PoliticalStatusEnum.values()) {
            if (politicalStatusEnum.getDescription().contains(political)) {
                dormitoryManager.setPoliticalStatus(politicalStatusEnum);
                break;
            }
        }
        String employmentStatus = convertCellString(row.getCell(excelDormitoryWorkStatusCellNumber));
        for (EmploymentStatusEnum employmentStatusEnum : EmploymentStatusEnum.values()) {
            if (employmentStatusEnum.getDescription().contains(employmentStatus)) {
                dormitoryManager.setEmploymentStatus(employmentStatusEnum);
                break;
            }
        }
        String education = convertCellString(row.getCell(excelDormitoryEducationCellNumber));
        for (EducationStatusEnum educationStatusEnum : EducationStatusEnum.values()) {
            if (educationStatusEnum.getDescription().contains(education)) {
                dormitoryManager.setEducation(educationStatusEnum);
                break;
            }
        }
        dormitoryManager.setAddress(convertCellString(row.getCell(excelDormitoryAddressCellNumber)));
        dormitoryManager.setManagerAddress(convertCellString(row.getCell(excelDormitoryManagerAddressCellNumber)));
        dormitoryManager.setManagerCount(convertCell(row.getCell(excelDormitoryManagerCountCellNumber)));
        String telephone = convertCellString(row.getCell(excelDormitoryTelephoneCellNumber));
        String landline = convertCellString(row.getCell(excelDormitoryLandlineCellNumber));
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhoneNumber(telephone).setPhoneType(PhoneTypeEnum.MOBILE).setSourceType(PhoneNumberSourceTypeEnum.DORMITORY_MANAGER).setSourceId(id);
        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setPhoneNumber(landline).setPhoneType(PhoneTypeEnum.LANDLINE).setSourceType(PhoneNumberSourceTypeEnum.DORMITORY_MANAGER).setSourceId(id);
        phoneNumbers.add(phoneNumber1);
        phoneNumbers.add(phoneNumber2);
        dormitoryManager.setPhoneNumbers(phoneNumbers);
        String subcontractorName = convertCellString(row.getCell(excelDormitorySubcontractorNameCellNumber));
        String subcontractorPhone = convertCellString(row.getCell(excelDormitorySubcontractorTelephoneCellNumber));
        Long subcontractorId = addSubcontractorHandler(subcontractorName, subcontractorPhone, subcontractors, communityId);
        dormitoryManager.setSubcontractorId(subcontractorId);
        return dormitoryManager;
    }
}
