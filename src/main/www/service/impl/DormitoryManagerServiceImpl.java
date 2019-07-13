package www.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.promeg.pinyinhelper.Pinyin;
import constant.PhoneCheckedTypes;
import constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import utils.DateUtil;
import utils.ExcelUtil;
import utils.StringCheckedRegexUtil;
import www.entity.DormitoryManager;
import www.entity.Subdistrict;
import www.entity.SystemUser;
import www.service.DormitoryManagerService;

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

    @Override
    public Map<String, Object> findDormitoryManagersAndCommunity(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) {
        List<Long> roleLocationIds = findCommunityIds(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, pageNumber, pageDataSize);
        List<DormitoryManager> data = dormitoryManagersDao.selectObjectsAndCommunityByCommunityIds(roleLocationIds);
        return findObjectsMethod(data);
    }

    @Override
    public DormitoryManager findDormitoryManagerAndCommunityById(String id) {
        DormitoryManager dormitoryManager = dormitoryManagersDao.selectObjectAndCommunityById(id);
        String[] phones = dormitoryManager.getTelephones().split(",");
        return phoneTypeHandler(phones, dormitoryManager);
    }

    @Override
    public void createDormitoryManager(DormitoryManager dormitoryManager) {
        StringBuilder phones = new StringBuilder();
        if (StringUtils.isNotEmpty(dormitoryManager.getTelephone())) {
            phones.append(dormitoryManager.getTelephone());
        }
        if (StringUtils.isNotEmpty(dormitoryManager.getLandline())) {
            if (phones.length() > 0) {
                phones.append(",");
            }
            phones.append(dormitoryManager.getLandline());
        }
        dormitoryManager.setTelephones(phones.toString());
        dormitoryManagersDao.insertObject(dormitoryManager);
    }

    @Override
    public void updateDormitoryManager(DormitoryManager dormitoryManager) {
        dormitoryManagersDao.updateObject(dormitoryManager);
    }

    @Override
    public int addDormitoryManagerFromExcel(Workbook workbook, Long subdistrictId, Map<String, Object> configurationsMap) throws Exception {
        List<DormitoryManager> dormitoryManagers = new ArrayList<>();
        Long readDormitoryExcelStartRowNumber = CommonUtil.convertConfigurationLong(configurationsMap.get("read_dormitory_excel_start_row_number"));
        excelDormitoryCommunityNameCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_community_name_cell_number"));
        excelDormitoryIdCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_id_cell_number"));
        excelDormitoryNameCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_name_cell_number"));
        excelDormitorySexCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_sex_cell_number"));
        excelDormitoryBirthCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_birth_cell_number"));
        excelDormitoryPoliticalStatusCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_political_status_cell_number"));
        excelDormitoryWorkStatusCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_work_status_cell_number"));
        excelDormitoryEducationCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_education_cell_number"));
        excelDormitoryAddressCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_address_cell_number"));
        excelDormitoryManagerAddressCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_manager_address_cell_number"));
        excelDormitoryManagerCountCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_manager_count_cell_number"));
        excelDormitoryTelephoneCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_telephone_cell_number"));
        excelDormitoryLandlineCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_landline_cell_number"));
        excelDormitorySubcontractorNameCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_subcontractor_name_cell_number"));
        excelDormitorySubcontractorTelephoneCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_dormitory_subcontractor_telephone_cell_number"));
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
            dormitoryManagersDao.deleteBySubdistrictId(subdistrictId);
            return dormitoryManagersDao.insertBatch(dormitoryManagers);
        }
        return 0;
    }

    @Override
    public Map<String, String> getPartStatHead() {
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
    public JSONArray findDormitoryManagersAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, List<Map<String, Object>> userData, String[] titles) {
        this.titles = titles;
        List<DormitoryManager> dormitoryManagers = dormitoryManagersDao.selectByUserData(userData, CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id")), CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id")));
        if (dormitoryManagers != null) {
            long index = 1L;
            Subdistrict subdistrict = subdistrictsDao.selectObjectById(dormitoryManagers.get(0).getCommunity().getSubdistrictId());
            if (titles[1].contains(SUBDISTRICT_NAME_PLACEHOLDER) && StringUtils.isNotEmpty(subdistrict.getSubdistrictName())) {
                String subdistrictName = subdistrict.getSubdistrictName();
                fileTitle = titles[1].replaceAll(SUBDISTRICT_NAME_REGEX, subdistrictName.replaceAll("(?iUs)[街道办事处]", ""));
            }
            for (DormitoryManager dormitoryManager : dormitoryManagers) {
                // 序号
                dormitoryManager.setSequenceNumber(index);
                // 处理社区名称
                String communityName = dormitoryManager.getCommunity().getCommunityName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
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
                if (dormitoryManager.getTelephones().contains(",")) {
                    String[] phones = dormitoryManager.getTelephones().split(",");
                    phoneTypeHandler(phones, dormitoryManager);
                } else {
                    phoneTypeHandler(new String[]{dormitoryManager.getTelephones()}, dormitoryManager);
                }
                // 处理分包人
                dormitoryManager.setSubcontractorName(dormitoryManager.getSubcontractor().getName());
                dormitoryManager.setSubcontractorTelephone(dormitoryManager.getSubcontractor().getTelephone());
                index++;
            }
        }
        return (JSONArray) JSON.toJSON(dormitoryManagers);
    }

    @Override
    public Map<String, Object> findDormitoryManagerByDormitoryManager(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, DormitoryManager dormitoryManager, Long companyId, Long companyRoleId, Integer pageNumber, Integer pageDataSize) {
        Map<String, Long> company = getCompany(systemUser, companyId, companyRoleId, pageNumber, pageDataSize);
        List<DormitoryManager> dormitoryManagers = dormitoryManagersDao.selectByUserRole(company.get("companyRoleId"), company.get("companyId"), systemRoleId, communityRoleId, subdistrictRoleId, dormitoryManager);
        return findObjectsMethod(dormitoryManagers);
    }

    @Override
    public Map<String, Object> setExcelHead() {
        ExcelUtil.DataHandler handler = (params, headers) -> {
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
            params.put("rowIndex", rowIndex + 2);
            params.put("workbook", workbook);
            params.put("sheet", sheet);
            params.put("contentStyle", contentStyle);
        };
        Map<String, Object> map = new HashMap<>(3);
        map.put("fileName", fileTitle);
        map.put("handler", handler);
        return map;
    }


    @Override
    public String findLastId(Long communityId, String communityName, String subdistrictName) {
        StringBuilder lastId = new StringBuilder();
        String id = dormitoryManagersDao.selectLastIdByCommunityId(communityId);
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
    public Map<String, Object> getBaseMessage(Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Map<String, Long> sexCount;
        Map<String, Long> ageData = null;
        Map<String, Long> educationData = null;
        Map<String, Long> politicalStatusData = null;
        Map<String, Long> workStatusData = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemRoleId);
        if (isSystemRole) {
            sexCount = dormitoryManagersDao.countSexAll();
            ageData = dormitoryManagersDao.countAgeRangeAll();
            educationData = dormitoryManagersDao.countEducationRangeAll();
            politicalStatusData = dormitoryManagersDao.countPoliticalStatusRangeAll();
            workStatusData = dormitoryManagersDao.countWorkStatusRangeAll();
        } else if (companyType.equals(communityRoleId)) {
            sexCount = dormitoryManagersDao.countSexByCommunityId(companyId);
            ageData = dormitoryManagersDao.countAgeRangeCommunityId(companyId);
            educationData = dormitoryManagersDao.countEducationRangeCommunityId(companyId);
            politicalStatusData = dormitoryManagersDao.countPoliticalStatusRangeCommunityId(companyId);
            workStatusData = dormitoryManagersDao.countWorkStatusRangeCommunityId(companyId);
        } else if (companyType.equals(subdistrictRoleId)) {
            sexCount = dormitoryManagersDao.countSexBySubdistrictId(companyId);
            ageData = dormitoryManagersDao.countAgeRangeSubdistrictId(companyId);
            educationData = dormitoryManagersDao.countEducationRangeSubdistrictId(companyId);
            politicalStatusData = dormitoryManagersDao.countPoliticalStatusRangeSubdistrictId(companyId);
            workStatusData = dormitoryManagersDao.countWorkStatusRangeSubdistrictId(companyId);
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
    public Map<String, Object> getChartBar(SystemUser systemUser, Long companyId, Long companyType, Boolean typeParam, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        String label = "社区楼长分包总户数";
        String companyLabel = null;
        LinkedList<Map<String, Object>> dormitoryManager;
        boolean isSystemRoleCount = subdistrictRoleId.equals(systemUser.getRoleId()) || (systemRoleId.equals(systemUser.getRoleId()) && subdistrictRoleId.equals(companyType));
        if (companyType == null || companyType.equals(systemRoleId)) {
            companyLabel = "街道";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagersDao.countForGroupSubdistrict();
            } else {
                dormitoryManager = dormitoryManagersDao.sumManagerCountForGroupSubdistrict();
            }
        } else if (isSystemRoleCount) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagersDao.countForGroupCommunity(companyId);
            } else {
                dormitoryManager = dormitoryManagersDao.sumManagerCountForGroupCommunity(companyId);
            }
        } else if (communityRoleId.equals(systemUser.getRoleId())) {
            companyLabel = "社区";
            if (typeParam) {
                label = "社区楼长总人数";
                dormitoryManager = dormitoryManagersDao.countForGroupByCommunityId(companyId);
            } else {
                dormitoryManager = dormitoryManagersDao.sumManagerCountForGroupByCommunityId(companyId);
            }
        } else {
            dormitoryManager = new LinkedList<>();
        }
        return barChartDataHandler(label, companyLabel, dormitoryManager);
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
        if (ExcelUtil.DEFAULT_DATE_PATTERN_EN.equals(birthCell.getCellStyle().getDataFormatString())) {
            birth = birthCell.getDateCellValue();
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String birthString = convertCellString(birthCell).replaceAll("(?iUs)^(\\d{4})[.-/年]?(\\d{1,2})[.-/月]?(?:\\d{1,2})?[日]?$", "$1-$2");
            birth = dateFormat.parse(birthString);
        }
        dormitoryManager.setBirth(birth);
        String political = convertCellString(row.getCell(excelDormitoryPoliticalStatusCellNumber));
        for (int k = 0; k < DormitoryManager.POLITICAL_STATUSES.length; k++) {
            if (DormitoryManager.POLITICAL_STATUSES[k].contains(political)) {
                dormitoryManager.setPoliticalStatus(k);
                break;
            }
        }
        String workStatus = convertCellString(row.getCell(excelDormitoryWorkStatusCellNumber));
        for (int k = 0; k < DormitoryManager.WORK_STATUSES.length; k++) {
            if (DormitoryManager.WORK_STATUSES[k].contains(workStatus)) {
                dormitoryManager.setWorkStatus(k);
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
        dormitoryManager.setTelephones(phones.toString());
        Timestamp editTime = DateUtil.getTimestamp(DateUtil.convertStringToDate(SystemConstant.DATABASE_START_TIMESTAMP_STRING));
        dormitoryManager.setEditTime(editTime);
        String subcontractorName = convertCellString(row.getCell(excelDormitorySubcontractorNameCellNumber));
        String subcontractorPhone = convertCellString(row.getCell(excelDormitorySubcontractorTelephoneCellNumber));
        Long subcontractorId = addSubcontractorHandler(subcontractorName, subcontractorPhone, subcontractors, communityId);
        dormitoryManager.setSubcontractorId(subcontractorId);
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
            if (StringCheckedRegexUtil.checkPhone(phone) == PhoneCheckedTypes.TELEPHONE) {
                dormitoryManager.setTelephone(phone);
            } else if (StringCheckedRegexUtil.checkPhone(phone) == PhoneCheckedTypes.LANDLINE) {
                dormitoryManager.setLandline(phone);
            }
        }
        return dormitoryManager;
    }
}
