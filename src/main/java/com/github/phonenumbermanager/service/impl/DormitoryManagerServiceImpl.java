package com.github.phonenumbermanager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.DateUtils;
import com.github.phonenumbermanager.utils.ExcelUtils;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 社区楼长Service实现
 *
 * @author 廿二月的天
 */
@Service("dormitoryManagerService")
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManager> implements DormitoryManagerService {

    private static final String SUBDISTRICT_NAME_PLACEHOLDER = "${subdistrictName}";
    private static final String SUBDISTRICT_NAME_REGEX = "(?Us)[${]{2}subdistrictName[}]";
    private static final String DATA_COLUMN_NAME = "人数";
    private Integer excelDormitoryCommunityNameCellNumber;
    private Integer excelDormitoryIdCellNumber;
    private Integer excelDormitoryNameCellNumber;
    private Integer excelDormitorySexCellNumber;
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
    private Pattern idPatten = Pattern.compile("(?iUs)([A-Za-z]+)(\\d+)");
    private ExcelUtils.DataHandler excelDataHandler = (params, headers) -> {
        Integer rowIndex = (Integer) params.get("rowIndex");
        Workbook workbook = (Workbook) params.get("workbook");
        Sheet sheet = (Sheet) params.get("sheet");
        // 附件格式
        if (StringUtils.isNotEmpty(titles[0])) {
            CellStyle attachStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 12, false, false, false);
            Row attachTitle = sheet.createRow(rowIndex);
            attachTitle.createCell(0).setCellValue(titles[0]);
            attachTitle.getCell(0).setCellStyle(attachStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
            sheet.getRow(rowIndex).setHeight((short) (15.6 * 20));
            rowIndex++;
        }
        // 表头
        Row titleRow = sheet.createRow(rowIndex);
        CellStyle titleStyle = ExcelUtils.setCellStyle(workbook, "方正小标宋简体", (short) 18, false, false, false);
        titleRow.createCell(0).setCellValue(fileTitle);
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, findPartStatHead().size() - 1));
        sheet.getRow(rowIndex).setHeight((short) (48 * 20));
        rowIndex++;
        CellStyle dateStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 11, false, false, false);
        // 单位、日期
        Row dateRow = sheet.createRow(rowIndex);
        int unitCellIndex = 10;
        int dateCellIndex = 12;
        dateRow.createCell(unitCellIndex).setCellValue("单位：户");
        dateRow.getCell(unitCellIndex).setCellStyle(dateStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, unitCellIndex, unitCellIndex + 1));
        dateRow.createCell(dateCellIndex).setCellValue("时间：" + DateUtils.date2Str("yyyy年MM月dd日", new Date()));
        dateRow.getCell(dateCellIndex).setCellStyle(dateStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, dateCellIndex, dateCellIndex + 3));
        sheet.getRow(rowIndex).setHeight((short) (26.1 * 20));
        rowIndex++;
        // 列头
        Row headerRow = sheet.createRow(rowIndex);
        CellStyle headerStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 11, false, true, false);
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
        CellStyle contentStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 9, false, true, true);
        rowIndex += 2;
        sheet.createFreezePane(0, rowIndex);
        params.put("rowIndex", rowIndex);
        params.put("workbook", workbook);
        params.put("sheet", sheet);
        params.put("contentStyle", contentStyle);
    };

    @Override
    public long update(DormitoryManager dormitoryManager) {
        if (communityDao.selectSubmittedById(0, dormitoryManager.getCommunityId())) {
            throw new BusinessException("此社区已经提报，不允许修改！");
        }
        dormitoryManager.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(dormitoryManager);
    }

    @Override
    public Map<String, Object> findCorrelation(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber, Integer pageDataSize) {
        List<Serializable> companyIds = findCommunityIds(systemUser, systemCompanyType, communityCompanyType, subdistrictCompanyType, pageNumber, pageDataSize);
        List<DormitoryManager> data = dormitoryManagerDao.selectAndCommunityByCommunityIds(companyIds);
        return find(data);
    }

    @Override
    public DormitoryManager findCorrelation(Serializable id) {
        DormitoryManager dormitoryManager = dormitoryManagerDao.selectAndCommunityById(id);
        String[] phones = dormitoryManager.getPhones().split(",");
        return phoneTypeHandler(phones, dormitoryManager);
    }

    @Override
    public long create(DormitoryManager dormitoryManager) {
        if (communityDao.selectSubmittedById(0, dormitoryManager.getCommunityId())) {
            throw new BusinessException("此社区已经提报，不允许添加！");
        }
        StringBuilder phones = new StringBuilder();
        if (StringUtils.isNotEmpty(dormitoryManager.getMobile())) {
            phones.append(dormitoryManager.getMobile());
        }
        if (StringUtils.isNotEmpty(dormitoryManager.getLandline())) {
            if (phones.length() > 0) {
                phones.append(",");
            }
            phones.append(dormitoryManager.getLandline());
        }
        dormitoryManager.setPhones(phones.toString());
        dormitoryManager.setCreateTime(DateUtils.getTimestamp(new Date()));
        dormitoryManager.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(dormitoryManager);
    }

    @Override
    public long create(Workbook workbook, Serializable subdistrictId, Map<String, Object> configurationsMap) throws Exception {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        Long readDormitoryExcelStartRowNumber = CommonUtils.convertConfigurationLong(configurationsMap.get("read_dormitory_excel_start_row_number"));
        excelDormitoryCommunityNameCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_community_name_cell_number"));
        excelDormitoryIdCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_id_cell_number"));
        excelDormitoryNameCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_name_cell_number"));
        excelDormitorySexCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_sex_cell_number"));
        excelDormitoryBirthCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_birth_cell_number"));
        excelDormitoryPoliticalStatusCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_political_status_cell_number"));
        excelDormitoryWorkStatusCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_work_status_cell_number"));
        excelDormitoryEducationCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_education_cell_number"));
        excelDormitoryAddressCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_address_cell_number"));
        excelDormitoryManagerAddressCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_manager_address_cell_number"));
        excelDormitoryManagerCountCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_manager_count_cell_number"));
        excelDormitoryTelephoneCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_telephone_cell_number"));
        excelDormitoryLandlineCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_landline_cell_number"));
        excelDormitorySubcontractorNameCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_subcontractor_name_cell_number"));
        excelDormitorySubcontractorTelephoneCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_dormitory_subcontractor_telephone_cell_number"));
        setCommunityVariables(subdistrictId);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readDormitoryExcelStartRowNumber - 1) {
                        dormitoryManagers.add(importExcelHandler(row));
                    }
                }
            }
        }
        if (dormitoryManagers.size() > 0) {
            dormitoryManagerDao.deleteBySubdistrictId(subdistrictId);
            return dormitoryManagerDao.insertBatch(dormitoryManagers);
        }
        return 0;
    }

    @Override
    public Map<String, String> findPartStatHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("sequenceNumber", "序号");
        tableHead.put("communityName", "社区名称");
        tableHead.put("id", "编号");
        tableHead.put("name", "姓名");
        tableHead.put("sexName", "性别");
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
    public JSONArray findCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData, String[] titles) {
        this.titles = titles;
        List<DormitoryManager> dormitoryManagers = dormitoryManagerDao.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        if (dormitoryManagers != null) {
            long index = 1L;
            Subdistrict subdistrict = subdistrictDao.selectById(dormitoryManagers.get(0).getCommunity().getSubdistrictId());
            if (titles[1].contains(SUBDISTRICT_NAME_PLACEHOLDER) && StringUtils.isNotEmpty(subdistrict.getName())) {
                String subdistrictName = subdistrict.getName();
                fileTitle = titles[1].replaceAll(SUBDISTRICT_NAME_REGEX, subdistrictName.replaceAll("(?iUs)[街道办事处]", ""));
            }
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                // 序号
                dormitoryManager.setSequenceNumber(index);
                // 处理社区名称
                String communityName = dormitoryManager.getCommunity().getName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                dormitoryManager.setCommunityName(communityName);
                // 性别处理
                dormitoryManager.setSexName(DormitoryManager.SEXES[dormitoryManager.getSex()]);
                // 出生年月
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
                dormitoryManager.setBirthString(dateFormat.format(dormitoryManager.getBirth()));
                // 政治面貌
                dormitoryManager.setPoliticalStatusName(DormitoryManager.POLITICAL_STATUSES[dormitoryManager.getPoliticalStatus()]);
                // 工作状况
                dormitoryManager.setWorkStatusName(DormitoryManager.WORK_STATUSES[dormitoryManager.getWorkStatus()]);
                // 文化程度
                dormitoryManager.setEducationName(DormitoryManager.EDUCATIONS[dormitoryManager.getEducation()]);
                // 处理多个联系方式
                if (dormitoryManager.getPhones().contains(",")) {
                    String[] phones = dormitoryManager.getPhones().split(",");
                    phoneTypeHandler(phones, dormitoryManager);
                } else {
                    phoneTypeHandler(new String[]{dormitoryManager.getPhones()}, dormitoryManager);
                }
                // 处理分包人
                dormitoryManager.setSubcontractorName(dormitoryManager.getSubcontractor().getName());
                dormitoryManager.setSubcontractorTelephone(dormitoryManager.getSubcontractor().getMobile());
                index++;
            }
        }
        return (JSONArray) JSON.toJSON(dormitoryManagers);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, DormitoryManager dormitoryManager, Serializable companyId, Serializable companyRoleId, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyRoleId, pageNumber, pageDataSize);
        List<DormitoryManager> dormitoryManagers = dormitoryManagerDao.selectByUserRole((Serializable) company.get("companyType"), (Serializable) company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType, dormitoryManager);
        return find(dormitoryManagers);
    }

    @Override
    public String find(Serializable communityId, String communityName, String subdistrictName) {
        StringBuilder lastId = new StringBuilder();
        String id = dormitoryManagerDao.selectLastIdByCommunityId(communityId);
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
    public Map<String, Object> find(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Map<String, Long> sexCount;
        Map<String, Long> ageData = null;
        Map<String, Long> educationData = null;
        Map<String, Long> politicalStatusData = null;
        Map<String, Long> workStatusData = null;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRole = companyType == null || companyId == null || ct == (int) systemCompanyType;
        if (isSystemRole) {
            sexCount = dormitoryManagerDao.countSexAll();
            ageData = dormitoryManagerDao.countAgeRangeAll();
            educationData = dormitoryManagerDao.countEducationRangeAll();
            politicalStatusData = dormitoryManagerDao.countPoliticalStatusRangeAll();
            workStatusData = dormitoryManagerDao.countWorkStatusRangeAll();
        } else if (ct == (int) communityCompanyType) {
            sexCount = dormitoryManagerDao.countSexByCommunityId(companyId);
            ageData = dormitoryManagerDao.countAgeRangeCommunityId(companyId);
            educationData = dormitoryManagerDao.countEducationRangeCommunityId(companyId);
            politicalStatusData = dormitoryManagerDao.countPoliticalStatusRangeCommunityId(companyId);
            workStatusData = dormitoryManagerDao.countWorkStatusRangeCommunityId(companyId);
        } else if (ct == (int) subdistrictCompanyType) {
            sexCount = dormitoryManagerDao.countSexBySubdistrictId(companyId);
            ageData = dormitoryManagerDao.countAgeRangeSubdistrictId(companyId);
            educationData = dormitoryManagerDao.countEducationRangeSubdistrictId(companyId);
            politicalStatusData = dormitoryManagerDao.countPoliticalStatusRangeSubdistrictId(companyId);
            workStatusData = dormitoryManagerDao.countWorkStatusRangeSubdistrictId(companyId);
        } else {
            sexCount = new HashMap<>(3);
            sexCount.put("male", 0L);
            sexCount.put("female", 0L);
        }
        baseMessage.put("sex", sexCount);
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
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Boolean typeParam, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String label = "社区楼长分包总户数";
        String companyLabel = null;
        LinkedList<Map<String, Object>> dormitoryManager;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getCompanyType()) || (systemCompanyType.equals(systemUser.getCompanyType()) && ct == (int) subdistrictCompanyType);
        if (companyType == null || ct == (int) systemCompanyType) {
            companyLabel = "街道";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerDao.countForGroupSubdistrict();
            } else {
                dormitoryManager = dormitoryManagerDao.sumManagerCountForGroupSubdistrict();
            }
        } else if (isSystemRoleCount) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerDao.countForGroupCommunity(companyId);
            } else {
                dormitoryManager = dormitoryManagerDao.sumManagerCountForGroupCommunity(companyId);
            }
        } else if ((int) communityCompanyType == systemUser.getCompanyType()) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagerDao.countForGroupByCommunityId(companyId);
            } else {
                dormitoryManager = dormitoryManagerDao.sumManagerCountForGroupByCommunityId(companyId);
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

    @Override
    public ExcelUtils.DataHandler getExcelDataHandler() {
        return excelDataHandler;
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
    private DormitoryManager importExcelHandler(Row row) throws ParseException {
        DormitoryManager dormitoryManager = new DormitoryManager();
        dormitoryManager.setId(convertCellString(row.getCell(excelDormitoryIdCellNumber)));
        String communityName = convertCellString(row.getCell(excelDormitoryCommunityNameCellNumber));
        Long communityId = getCommunityId(communityMap, communityName);
        dormitoryManager.setCommunityId(communityId);
        dormitoryManager.setName(convertCellString(row.getCell(excelDormitoryNameCellNumber)));
        int sex = 0;
        String sexName = convertCellString(row.getCell(excelDormitorySexCellNumber));
        if (DormitoryManager.SEXES[1].equals(sexName)) {
            sex = 1;
        } else if (!DormitoryManager.SEXES[0].equals(sexName)) {
            sex = 2;
        }
        dormitoryManager.setSex(sex);
        Cell birthCell = row.getCell(excelDormitoryBirthCellNumber);
        Date birth;
        if (ExcelUtils.DEFAULT_DATE_PATTERN_EN.equals(birthCell.getCellStyle().getDataFormatString())) {
            birth = birthCell.getDateCellValue();
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String birthString = convertCellString(birthCell).replaceAll("(?iUs)^(\\d{4})[.-/年]?(\\d{1,2})[.-/月]?(?:\\d{1,2})?[日]?$", "$1-$2");
            birth = dateFormat.parse(birthString);
        }
        dormitoryManager.setBirth(birth);
        String political = convertCellString(row.getCell(excelDormitoryPoliticalStatusCellNumber));
        for (int i = 0; i < DormitoryManager.POLITICAL_STATUSES.length; i++) {
            if (DormitoryManager.POLITICAL_STATUSES[i].contains(political)) {
                dormitoryManager.setPoliticalStatus(i);
                break;
            }
        }
        String workStatus = convertCellString(row.getCell(excelDormitoryWorkStatusCellNumber));
        for (int i = 0; i < DormitoryManager.WORK_STATUSES.length; i++) {
            if (DormitoryManager.WORK_STATUSES[i].contains(workStatus)) {
                dormitoryManager.setWorkStatus(i);
                break;
            }
        }
        String education = convertCellString(row.getCell(excelDormitoryEducationCellNumber));
        for (int k = 0; k < DormitoryManager.EDUCATIONS.length; k++) {
            if (DormitoryManager.EDUCATIONS[k].contains(education)) {
                dormitoryManager.setEducation(k);
                break;
            }
        }
        dormitoryManager.setAddress(convertCellString(row.getCell(excelDormitoryAddressCellNumber)));
        dormitoryManager.setManagerAddress(convertCellString(row.getCell(excelDormitoryManagerAddressCellNumber)));
        dormitoryManager.setManagerCount(convertCell(row.getCell(excelDormitoryManagerCountCellNumber)));
        String telephone = convertCellString(row.getCell(excelDormitoryTelephoneCellNumber));
        String landline = convertCellString(row.getCell(excelDormitoryLandlineCellNumber));
        StringBuilder phones = new StringBuilder();
        if (StringUtils.isNotEmpty(telephone)) {
            phones.append(telephone);
        }
        if (StringUtils.isNotEmpty(landline)) {
            if (phones.length() > 0) {
                phones.append(",");
            }
            phones.append(landline);
        }
        dormitoryManager.setPhones(phones.toString());
        Timestamp timestamp = DateUtils.getTimestamp(new Date());
        String subcontractorName = convertCellString(row.getCell(excelDormitorySubcontractorNameCellNumber));
        String subcontractorPhone = convertCellString(row.getCell(excelDormitorySubcontractorTelephoneCellNumber));
        Long subcontractorId = addSubcontractorHandler(subcontractorName, subcontractorPhone, subcontractors, communityId);
        dormitoryManager.setSubcontractorId(subcontractorId);
        dormitoryManager.setCreateTime(timestamp);
        dormitoryManager.setUpdateTime(timestamp);
        return dormitoryManager;
    }

    /**
     * 根据联系方式类型设置
     *
     * @param phones           联系方式数组
     * @param dormitoryManager 楼长对象
     * @return 社区居民楼长对象
     */
    private DormitoryManager phoneTypeHandler(String[] phones, DormitoryManager dormitoryManager) {
        for (String phone : phones) {
            if (StringCheckedRegexUtils.checkPhone(phone) == PhoneCheckedTypes.MOBILE) {
                dormitoryManager.setMobile(phone);
            } else if (StringCheckedRegexUtils.checkPhone(phone) == PhoneCheckedTypes.LANDLINE) {
                dormitoryManager.setLandline(phone);
            }
        }
        return dormitoryManager;
    }
}
