package www.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import constant.SystemConstant;
import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import utils.DateUtil;
import utils.ExcelUtil;
import www.entity.CommunityResident;
import www.entity.SystemUser;
import www.service.CommunityResidentService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResident> implements CommunityResidentService {

    private Pattern communityPattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");

    @Override
    public CommunityResident findCommunityResidentAndCommunityById(Long id) {
        CommunityResident communityResident = communityResidentsDao.selectObjectAndCommunityById(id);
        String[] residentPhones = communityResident.getCommunityResidentPhones().split(",");
        switch (residentPhones.length) {
            case 1:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                break;
            case 2:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                communityResident.setCommunityResidentPhone2(residentPhones[1]);
                break;
            case 3:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                communityResident.setCommunityResidentPhone2(residentPhones[1]);
                communityResident.setCommunityResidentPhone3(residentPhones[2]);
                break;
            default:
                throw new BusinessException("联系方式处理错误！");
        }
        return communityResident;
    }

    @Override
    public void createCommunityResident(CommunityResident communityResident) {
        communityResidentsDao.insertObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public void updateCommunityResident(CommunityResident communityResident) {
        communityResidentsDao.updateObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, CommunityResident communityResident, Long companyId, Long companyRoleId, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        List<CommunityResident> communityResidents = null;
        if (companyRoleId != null && companyRoleId.equals(communityRoleId)) {
            // 社区级别
            communityResident.setCommunityId(companyId);
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
        } else if (companyRoleId != null && companyRoleId.equals(subdistrictRoleId)) {
            // 街道级别
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
        } else {
            // 未输入单位类型
            if (systemUser.getRoleId().equals(systemRoleId)) {
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
            } else if (systemUser.getRoleId().equals(subdistrictRoleId)) {
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
            } else if (systemUser.getRoleId().equals(communityRoleId)) {
                communityResident.setCommunityId(systemUser.getRoleLocationId());
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
                PageHelper.startPage(pageNumber, pageDataSize);
            }
        }
        return findObjectsMethod(communityResidents);
    }

    @Override
    public int addCommunityResidentFromExcel(Workbook workbook, Long subdistrictId, Map<String, Object> configurationsMap) throws ParseException {
        List<CommunityResident> residents = new ArrayList<>();
        Long readResidentExcelStartRowNumber = CommonUtil.convertConfigurationLong(configurationsMap.get("read_resident_excel_start_row_number"));
        Integer excelCommunityCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_community_name_cell_number"));
        Integer excelCommunityResidentNameCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_name_cell_number"));
        Integer excelResidentAddressCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_address_cell_number"));
        Integer excelPhone1CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_phone1_cell_number"));
        Integer excelPhone2CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_phone2_cell_number"));
        Integer excelPhone3CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_phone3_cell_number"));
        Integer excelSubcontractorCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_resident_subcontractor_name_cell_number"));
        setCommunityVariables(subdistrictId);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readResidentExcelStartRowNumber) {
                        CommunityResident resident = new CommunityResident();
                        // 居民姓名
                        resident.setCommunityResidentName(convertCellString(row.getCell(excelCommunityResidentNameCellNumber)));
                        // 居民地址
                        // 处理地址和社区名称
                        String address = convertCellString(row.getCell(excelResidentAddressCellNumber));
                        Matcher matcher = communityPattern.matcher(address);
                        String realAddress = null;
                        while (matcher.find()) {
                            realAddress = matcher.group(2);
                        }
                        resident.setCommunityResidentAddress(realAddress);
                        // 居民电话三个合一
                        Cell phone1Cell = row.getCell(excelPhone1CellNumber);
                        Cell phone2Cell = row.getCell(excelPhone2CellNumber);
                        Cell phone3Cell = row.getCell(excelPhone3CellNumber);
                        String phones = residentPhoneHandler(phone1Cell, phone2Cell, phone3Cell);
                        resident.setCommunityResidentPhones(phones);
                        // 社区名称
                        String communityName = convertCellString(row.getCell(excelCommunityCellNumber));
                        Long communityId = getCommunityId(communityMap, communityName);
                        // 分包人
                        String subcontractorName = convertCellString(row.getCell(excelSubcontractorCellNumber)).replaceAll(communityName, "");
                        Long subcontractorId = addSubcontractorHandler(subcontractorName, "", subcontractors, communityId);
                        resident.setSubcontractorId(subcontractorId);
                        resident.setCommunityId(communityId);
                        Timestamp editTime = DateUtil.getTimestamp(DateUtil.convertStringToDate(SystemConstant.DATABASE_START_TIMESTAMP_STRING));
                        resident.setEditTime(editTime);
                        residents.add(resident);
                    }
                }
            }
        }
        if (residents.size() > 0) {
            communityResidentsDao.deleteBySubdistrictId(subdistrictId);
            return communityResidentsDao.insertBatch(residents);
        }
        return 0;
    }

    @Override
    public Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) {
        List<Long> roleLocationIds = findCommunityIds(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, pageNumber, pageDataSize);
        List<CommunityResident> data = communityResidentsDao.selectObjectsAndCommunityByCommunityIds(roleLocationIds);
        return findObjectsMethod(data);
    }

    @Override
    public JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, List<Map<String, Object>> userData) {
        List<CommunityResident> communityResidents = communityResidentsDao.selectByUserData(userData, CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id")), CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id")));
        if (communityResidents != null) {
            for (CommunityResident communityResident : communityResidents) {
                String communityName = communityResident.getCommunity().getCommunityName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getSubdistrictName().replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "").replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                communityResident.setSubdistrictName(subdistrictName);
                communityResident.setCommunityName(communityName);
                // 处理多个联系方式
                if (communityResident.getCommunityResidentPhones().contains(",")) {
                    String[] phones = communityResident.getCommunityResidentPhones().split(",");
                    if (phones.length == 2) {
                        communityResident.setCommunityResidentPhone1(phones[0]);
                        communityResident.setCommunityResidentPhone2(phones[1]);
                    } else {
                        communityResident.setCommunityResidentPhone1(phones[0]);
                        communityResident.setCommunityResidentPhone2(phones[1]);
                        communityResident.setCommunityResidentPhone3(phones[2]);
                    }
                } else {
                    communityResident.setCommunityResidentPhone1(communityResident.getCommunityResidentPhones());
                }
                // 处理地址
                // 处理分包人
                communityResident.setSubcontractorName(communityName + communityResident.getSubcontractor().getName());
            }
        }
        return (JSONArray) JSON.toJSON(communityResidents);
    }

    @Override
    public Map<String, String> getPartStatHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("subdistrictName", "街道");
        tableHead.put("communityName", "社区");
        tableHead.put("communityResidentName", "户主姓名");
        tableHead.put("communityResidentAddress", "家庭地址");
        tableHead.put("communityResidentPhone1", "电话1");
        tableHead.put("communityResidentPhone2", "电话2");
        tableHead.put("communityResidentPhone3", "电话3");
        tableHead.put("subcontractorName", "分包人");
        return tableHead;
    }

    @Override
    public Map<String, Object> computedCount(SystemUser systemUser, Integer getType, Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> computed = new HashMap<>(3);
        if (getType == null || getType == 0) {
            computed.put("baseMessage", getBaseMessage(companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
            computed.put("barChart", getChartBar(systemUser, companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        } else if (getType == 1) {
            computed.put("baseMessage", getBaseMessage(companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        } else {
            computed.put("barChart", getChartBar(systemUser, companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        }
        return computed;
    }

    @Override
    public ExcelUtil.DataHandler setExcelHead(String[] titles) {
        return (params, headers) -> {
            Integer rowIndex = (Integer) params.get("rowIndex");
            Workbook workbook = (Workbook) params.get("workbook");
            Sheet sheet = (Sheet) params.get("sheet");
            // 附件格式
            if (StringUtils.isNotEmpty(titles[0])) {
                Row attachTitle = sheet.createRow(rowIndex);
                CellStyle attachStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 12, false, false, false);
                int attachCellIndex = 0;
                attachTitle.createCell(attachCellIndex).setCellValue(titles[0]);
                attachTitle.getCell(attachCellIndex).setCellStyle(attachStyle);
                rowIndex++;
            }
            // 表头
            Row titleRow = sheet.createRow(rowIndex);
            int titleCellIndex = 0;
            titleRow.createCell(titleCellIndex).setCellValue(titles[1]);
            CellStyle titleStyle = ExcelUtil.setCellStyle(workbook, "方正小标宋简体", (short) 16, false, false, false);
            titleRow.getCell(titleCellIndex).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, getPartStatHead().size() - 1));
            sheet.getRow(rowIndex).setHeight((short) (27.8 * 20));
            rowIndex++;
            // 日期
            Row dateRow = sheet.createRow(rowIndex);
            int dateCellIndex = 6;
            dateRow.createCell(dateCellIndex).setCellValue("时间：" + DateUtil.date2Str("yyyy年MM月dd日", new Date()));
            CellStyle dateStyle = ExcelUtil.setCellStyle(workbook, "宋体", (short) 11, false, false, true);
            dateRow.getCell(dateCellIndex).setCellStyle(dateStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 6, 7));
            sheet.getRow(rowIndex).setHeight((short) (27.8 * 20));
            rowIndex++;
            // 列头
            Row headerRow = sheet.createRow(rowIndex);
            CellStyle headerStyle = ExcelUtil.setCellStyle(workbook, "黑体", (short) 12, false, true, true);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
            }
            sheet.setAutoFilter(new CellRangeAddress(rowIndex, rowIndex, 0, headers.length - 1));
            headerRow.setHeight((short) (21.8 * 20));
            CellStyle contentStyle = ExcelUtil.setCellStyle(workbook, "仿宋_GB2312", (short) 10, false, true, true);
            params.put("rowIndex", rowIndex + 2);
            params.put("workbook", workbook);
            params.put("sheet", sheet);
            params.put("contentStyle", contentStyle);
        };
    }

    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident 需要处理的社区居民对象
     * @return 处理后的社区居民对象
     */
    private CommunityResident multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setCommunityResidentName(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident.setCommunityResidentAddress(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentAddress())).replaceAll("—", "-"));
        // 联系方式数组处理
        StringBuilder tempPhone = new StringBuilder();
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone1())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone1())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone2())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone2())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone3())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone3())).replaceAll("—", "-")).append(",");
        }
        communityResident.setCommunityResidentPhones(tempPhone.toString().substring(0, tempPhone.length() - 1));
        // 编辑时间
        communityResident.setEditTime(DateUtil.getTimestamp(new Date()));
        return communityResident;
    }

    /**
     * 社区居民联系方式处理
     *
     * @param phone1Cell 联系方式一单元格对象
     * @param phone2Cell 联系方式二单元格对象
     * @param phone3Cell 联系方式三单元格对象
     * @return 联系方式拼接字符串
     */
    private String residentPhoneHandler(Cell phone1Cell, Cell phone2Cell, Cell phone3Cell) {
        String phone1 = convertCellString(phone1Cell);
        String phone2 = convertCellString(phone2Cell);
        String phone3 = convertCellString(phone3Cell);
        StringBuilder phones = new StringBuilder();
        if (!StringUtils.isEmpty(phone1)) {
            phones.append(phone1);
        }
        if (!StringUtils.isEmpty(phone2)) {
            phones.append(",").append(phone2);
        }
        if (!StringUtils.isEmpty(phone3)) {
            phones.append(",").append(phone3);
        }
        return phones.toString();
    }

    /**
     * 获取录入统计信息
     *
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 统计信息对象
     */
    private Map<String, Object> getBaseMessage(Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Long addCount = null;
        Long haveToCount = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemRoleId);
        if (isSystemRole) {
            addCount = communityResidentsDao.countCommunityResidents();
            haveToCount = communitiesDao.sumActualNumber();
        } else if (companyType.equals(communityRoleId)) {
            addCount = communityResidentsDao.countObjectByCommunityId(companyId);
            haveToCount = communitiesDao.selectActualNumberByCommunityId(companyId);
        } else if (companyType.equals(subdistrictRoleId)) {
            addCount = communityResidentsDao.countCommunityResidentsBySubdistrictId(companyId);
            haveToCount = communitiesDao.sumActualNumberBySubdistrictId(companyId);
        }
        baseMessage.put("addCount", addCount);
        baseMessage.put("haveToCount", haveToCount);
        return baseMessage;
    }

    /**
     * 获取柱状图数据
     *
     * @param systemUser        正在登录中的系统用户对象
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 柱状图数据
     */
    private Map<String, Object> getChartBar(SystemUser systemUser, Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> barChartMap = new HashMap<>(3);
        List<String> columns = new ArrayList<>();
        String companyLabel;
        String label = "社区居民人数";
        Map<String, Map<String, Object>> communityResidents;
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> data = new HashMap<>(3);
        List<String> titleLabel = new ArrayList<>();
        if (companyType == null || companyType.equals(systemRoleId)) {
            companyLabel = "街道";
            communityResidents = communityResidentsDao.countCommunityResidentsForGroupSubdistrict();
        } else if (communityRoleId.equals(systemUser.getRoleId())) {
            companyLabel = "社区";
            communityResidents = communityResidentsDao.countCommunityResidentsGroupByCommunityId(companyId);
        } else {
            companyLabel = "社区";
            communityResidents = communityResidentsDao.countCommunityResidentsForGroupCommunity(companyId);
        }
        for (Map<String, Object> residentCount : communityResidents.values()) {
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
}
