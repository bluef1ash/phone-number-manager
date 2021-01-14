package com.github.phonenumbermanager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.DateUtils;
import com.github.phonenumbermanager.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
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
    private final Pattern communityPattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");

    @Override
    public CommunityResident findCorrelation(Serializable id) {
        CommunityResident communityResident = communityResidentDao.selectAndCommunityById(id);
        String[] residentPhones = communityResident.getPhones().split(",");
        switch (residentPhones.length) {
            case 1:
                communityResident.setPhone1(residentPhones[0]);
                break;
            case 2:
                communityResident.setPhone1(residentPhones[0]);
                communityResident.setPhone2(residentPhones[1]);
                break;
            case 3:
                communityResident.setPhone1(residentPhones[0]);
                communityResident.setPhone2(residentPhones[1]);
                communityResident.setPhone3(residentPhones[2]);
                break;
            default:
                throw new BusinessException("联系方式处理错误！");
        }
        return communityResident;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long create(CommunityResident communityResident) {
        if (communityDao.selectSubmittedById(0, communityResident.getCommunityId())) {
            throw new BusinessException("此社区已经提报，不允许添加！");
        }
        communityResident.setCreateTime(DateUtils.getTimestamp(new Date()));
        multiplePhoneHandler(communityResident);
        return super.create(communityResident);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long update(CommunityResident communityResident) {
        if (communityDao.selectSubmittedById(0, communityResident.getCommunityId())) {
            throw new BusinessException("此社区已经提报，不允许修改！");
        }
        multiplePhoneHandler(communityResident);
        return super.update(communityResident);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, CommunityResident communityResident, Serializable companyId, Serializable companyType, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyType, pageNumber, pageDataSize);
        List<CommunityResident> communityResidents = communityResidentDao.selectByUserRole((Serializable) company.get("companyType"), (Serializable) company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType, communityResident);
        return find(communityResidents);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long create(Workbook workbook, Serializable subdistrictId, Map<String, Object> configurationsMap) {
        List<CommunityResident> residents = new ArrayList<>();
        long readResidentExcelStartRowNumber = CommonUtils.convertConfigurationLong(configurationsMap.get("read_resident_excel_start_row_number"));
        int excelCommunityCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_community_name_cell_number"));
        int excelCommunityResidentNameCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_name_cell_number"));
        int excelResidentAddressCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_address_cell_number"));
        int excelPhone1CellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_phone1_cell_number"));
        int excelPhone2CellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_phone2_cell_number"));
        int excelPhone3CellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_phone3_cell_number"));
        int excelSubcontractorCellNumber = CommonUtils.convertConfigurationInteger(configurationsMap.get("excel_resident_subcontractor_name_cell_number"));
        Timestamp timestamp = DateUtils.getTimestamp(new Date());
        setCommunityVariables(subdistrictId);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readResidentExcelStartRowNumber) {
                        CommunityResident resident = new CommunityResident();
                        // 居民姓名
                        resident.setName(convertCellString(row.getCell(excelCommunityResidentNameCellNumber)));
                        // 居民地址
                        // 处理地址和社区名称
                        String address = convertCellString(row.getCell(excelResidentAddressCellNumber));
                        Matcher matcher = communityPattern.matcher(address);
                        String realAddress = null;
                        while (matcher.find()) {
                            realAddress = matcher.group(2);
                        }
                        resident.setAddress(realAddress);
                        // 居民电话三个合一
                        Cell phone1Cell = row.getCell(excelPhone1CellNumber);
                        Cell phone2Cell = row.getCell(excelPhone2CellNumber);
                        Cell phone3Cell = row.getCell(excelPhone3CellNumber);
                        String phones = residentPhoneHandler(phone1Cell, phone2Cell, phone3Cell);
                        resident.setPhones(phones);
                        // 社区名称
                        String communityName = convertCellString(row.getCell(excelCommunityCellNumber));
                        Long communityId = getCommunityId(communityMap, communityName);
                        // 分包人
                        String subcontractorName = convertCellString(row.getCell(excelSubcontractorCellNumber)).replaceAll(communityName, "");
                        Long subcontractorId = addSubcontractorHandler(subcontractorName, "", subcontractors, communityId);
                        resident.setSubcontractorId(subcontractorId);
                        resident.setCommunityId(communityId);
                        resident.setCreateTime(timestamp);
                        resident.setUpdateTime(timestamp);
                        residents.add(resident);
                    }
                }
            }
        }
        if (residents.size() > 0) {
            communityResidentDao.deleteBySubdistrictId(subdistrictId);
            return communityResidentDao.insertBatch(residents);
        }
        return 0;
    }

    @Override
    public JSONArray findCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData) {
        List<CommunityResident> communityResidents = communityResidentDao.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        if (communityResidents != null && communityResidents.size() > 0) {
            for (CommunityResident communityResident : communityResidents) {
                String communityName = communityResident.getCommunity().getName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getName().replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "").replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                communityResident.setSubdistrictName(subdistrictName);
                communityResident.setCommunityName(communityName);
                // 处理多个联系方式
                if (communityResident.getPhones().contains(",")) {
                    String[] phones = communityResident.getPhones().split(",");
                    if (phones.length == 2) {
                        communityResident.setPhone1(phones[0]);
                        communityResident.setPhone2(phones[1]);
                    } else {
                        communityResident.setPhone1(phones[0]);
                        communityResident.setPhone2(phones[1]);
                        communityResident.setPhone3(phones[2]);
                    }
                } else {
                    communityResident.setPhone1(communityResident.getPhones());
                }
                // 处理地址
                // 处理分包人
                communityResident.setSubcontractorName(communityName + communityResident.getSubcontractor().getName());
            }
        }
        return (JSONArray) JSON.toJSON(communityResidents);
    }

    @Override
    public Map<String, String> findPartStatHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("subdistrictName", "街道");
        tableHead.put("communityName", "社区");
        tableHead.put("name", "户主姓名");
        tableHead.put("address", "家庭地址");
        tableHead.put("phone1", "电话1");
        tableHead.put("phone2", "电话2");
        tableHead.put("phone3", "电话3");
        tableHead.put("subcontractorName", "分包人");
        return tableHead;
    }

    @Override
    public ExcelUtils.DataHandler setExcelHead(String[] titles) {
        return (params, headers) -> {
            Integer rowIndex = (Integer) params.get("rowIndex");
            Workbook workbook = (Workbook) params.get("workbook");
            Sheet sheet = (Sheet) params.get("sheet");
            // 附件格式
            if (StringUtils.isNotEmpty(titles[0])) {
                Row attachTitle = sheet.createRow(rowIndex);
                CellStyle attachStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 12, false, false, false);
                int attachCellIndex = 0;
                attachTitle.createCell(attachCellIndex).setCellValue(titles[0]);
                attachTitle.getCell(attachCellIndex).setCellStyle(attachStyle);
                rowIndex++;
            }
            // 表头
            Row titleRow = sheet.createRow(rowIndex);
            int titleCellIndex = 0;
            titleRow.createCell(titleCellIndex).setCellValue(titles[1]);
            CellStyle titleStyle = ExcelUtils.setCellStyle(workbook, "方正小标宋简体", (short) 16, false, false, false);
            titleRow.getCell(titleCellIndex).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, findPartStatHead().size() - 1));
            sheet.getRow(rowIndex).setHeight((short) (27.8 * 20));
            rowIndex++;
            // 日期
            Row dateRow = sheet.createRow(rowIndex);
            int dateCellIndex = 6;
            dateRow.createCell(dateCellIndex).setCellValue("时间：" + DateUtils.date2Str("yyyy年MM月dd日", new Date()));
            CellStyle dateStyle = ExcelUtils.setCellStyle(workbook, "宋体", (short) 11, false, false, true);
            dateRow.getCell(dateCellIndex).setCellStyle(dateStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 6, 7));
            sheet.getRow(rowIndex).setHeight((short) (27.8 * 20));
            rowIndex++;
            // 列头
            Row headerRow = sheet.createRow(rowIndex);
            CellStyle headerStyle = ExcelUtils.setCellStyle(workbook, "黑体", (short) 12, false, true, true);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
            }
            sheet.setAutoFilter(new CellRangeAddress(rowIndex, rowIndex, 0, headers.length - 1));
            headerRow.setHeight((short) (21.8 * 20));
            CellStyle contentStyle = ExcelUtils.setCellStyle(workbook, "仿宋_GB2312", (short) 10, false, true, true);
            rowIndex++;
            sheet.createFreezePane(0, rowIndex);
            params.put("rowIndex", rowIndex);
            params.put("workbook", workbook);
            params.put("sheet", sheet);
            params.put("contentStyle", contentStyle);
        };
    }

    @Override
    public Map<String, Object> find(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Long addCount = null;
        Long haveToCount = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemCompanyType);
        long ct = (long) (companyType == null ? 0L : companyType);
        int cct = (int) communityCompanyType;
        int sct = (int) subdistrictCompanyType;
        if (isSystemRole) {
            addCount = communityResidentDao.count();
            haveToCount = communityDao.sumActualNumber();
        } else if (ct == cct) {
            addCount = communityResidentDao.countByCommunityId(companyId);
            haveToCount = communityDao.selectActualNumberById(companyId);
        } else if (ct == sct) {
            addCount = communityResidentDao.countBySubdistrictId(companyId);
            haveToCount = communityDao.sumActualNumberBySubdistrictId(companyId);
        }
        baseMessage.put("addCount", addCount);
        baseMessage.put("haveToCount", haveToCount);
        return baseMessage;
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String companyLabel = "社区";
        LinkedList<Map<String, Object>> communityResidents;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getCompanyType()) || (systemCompanyType.equals(systemUser.getCompanyType()) && ct == (int) subdistrictCompanyType);
        if (companyType == null || ct == (int) systemCompanyType) {
            companyLabel = "街道";
            communityResidents = communityResidentDao.countForGroupSubdistrict();
        } else if (isSystemRoleCount) {
            communityResidents = communityResidentDao.countForGroupCommunity(companyId);
        } else if ((int) communityCompanyType == (long) systemUser.getCompanyType()) {
            communityResidents = communityResidentDao.countForGroupByCommunityId(companyId);
        } else {
            communityResidents = new LinkedList<>();
        }
        return barChartDataHandler("社区居民总户数", companyLabel, "户", communityResidents);
    }

    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident 需要处理的社区居民对象
     */
    private void multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setName(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident.setAddress(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getAddress())).replaceAll("—", "-"));
        // 联系方式数组处理
        StringBuilder tempPhone = new StringBuilder();
        if (!StringUtils.isEmpty(communityResident.getPhone1())) {
            tempPhone.append(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone1())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getPhone2())) {
            tempPhone.append(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone2())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getPhone3())) {
            tempPhone.append(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone3())).replaceAll("—", "-")).append(",");
        }
        communityResident.setPhones(tempPhone.substring(0, tempPhone.length() - 1));
        // 编辑时间
        communityResident.setUpdateTime(DateUtils.getTimestamp(new Date()));
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
}
