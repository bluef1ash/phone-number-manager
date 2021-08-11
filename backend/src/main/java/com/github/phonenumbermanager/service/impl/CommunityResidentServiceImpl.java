package com.github.phonenumbermanager.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.CommunityResidentMapper;
import com.github.phonenumbermanager.service.CommunityResidentService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident> implements CommunityResidentService {
    private final Pattern communityPattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");

    @Override
    public CommunityResident getCorrelation(Serializable id) {
        return communityResidentMapper.selectAndCommunityById(id);
    }

    @Override
    public IPage<CommunityResident> get(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, CommunityResident communityResident, Serializable companyId, Serializable companyType, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyType);
        Page<CommunityResident> page = new Page<>(pageNumber, pageDataSize);
        return communityResidentMapper.selectByUserRole(page, (Serializable) company.get("companyType"), (Serializable) company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType, communityResident);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap) {
        List<CommunityResident> residents = new ArrayList<>();
        long readResidentExcelStartRowNumber = Convert.toLong(configurationsMap.get("read_resident_excel_start_row_number"));
        int excelCommunityCellNumber = Convert.toInt(configurationsMap.get("excel_resident_community_name_cell_number"));
        int excelCommunityResidentNameCellNumber = Convert.toInt(configurationsMap.get("excel_resident_name_cell_number"));
        int excelResidentAddressCellNumber = Convert.toInt(configurationsMap.get("excel_resident_address_cell_number"));
        int excelPhone1CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone1_cell_number"));
        int excelPhone2CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone2_cell_number"));
        int excelPhone3CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone3_cell_number"));
        int excelSubcontractorCellNumber = Convert.toInt(configurationsMap.get("excel_resident_subcontractor_name_cell_number"));
        setCommunityVariables(subdistrictId);
        /*for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readResidentExcelStartRowNumber) {
                        CommunityResident resident = new CommunityResident();
                        long id = IdWorker.getId(resident);
                        resident.setId(id);
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
                        List<PhoneNumber> phoneNumbers = residentPhoneHandler(String.valueOf(id), phone1Cell, phone2Cell, phone3Cell);
                        resident.setPhoneNumbers(phoneNumbers);
                        // 社区名称
                        String communityName = convertCellString(row.getCell(excelCommunityCellNumber));
                        Long communityId = getCommunityId(communityMap, communityName);
                        // 分包人
                        String subcontractorName = convertCellString(row.getCell(excelSubcontractorCellNumber)).replaceAll(communityName, "");
                        Long subcontractorId = addSubcontractorHandler(subcontractorName, "", subcontractors, communityId);
                        resident.setSubcontractorId(subcontractorId);
                        resident.setCommunityId(communityId);
                        residents.add(resident);
                    }
                }
            }
        }*/
        if (residents.size() > 0) {
            QueryWrapper<CommunityResident> wrapper = new QueryWrapper<>();
            wrapper.eq("subdistrict_id", subdistrictId);
            communityResidentMapper.delete(wrapper);
            return saveBatch(residents);
        }
        return false;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        List<CommunityResident> communityResidents = communityResidentMapper.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        if (communityResidents != null && communityResidents.size() > 0) {
            for (CommunityResident communityResident : communityResidents) {
                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                String communityName = communityResident.getCommunity().getName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getName().replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "").replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                hashMap.put("街道", subdistrictName);
                hashMap.put("社区", communityName);
                hashMap.put("户主姓名", communityResident.getName());
                hashMap.put("家庭地址", communityResident.getAddress());
                for (int i = 0; i < communityResident.getPhoneNumbers().size(); i++) {
                    hashMap.put("电话" + (i + 1), communityResident.getPhoneNumbers().get(i));
                }
                // 处理分包人
                hashMap.put("分包人", communityName + communityResident.getSubcontractor().getName());
                list.add(hashMap);
            }
        }
        return list;
    }

    /*@Override
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
            rowIndex++;
            sheet.createFreezePane(0, rowIndex);
            params.put("rowIndex", rowIndex);
            params.put("workbook", workbook);
            params.put("sheet", sheet);
            params.put("contentStyle", contentStyle);
        };
    }*/

    @Override
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Long addCount = null;
        Long haveToCount = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemCompanyType);
        long ct = (long) (companyType == null ? 0L : companyType);
        int cct = (int) communityCompanyType;
        int sct = (int) subdistrictCompanyType;
        if (isSystemRole) {
            addCount = (long) count();
            haveToCount = communityMapper.sumActualNumber();
        } else if (ct == cct) {
            addCount = communityResidentMapper.countByCommunityId(companyId);
            haveToCount = communityMapper.selectActualNumberById(companyId);
        } else if (ct == sct) {
            addCount = communityResidentMapper.countBySubdistrictId(companyId);
            haveToCount = communityMapper.sumActualNumberBySubdistrictId(companyId);
        }
        baseMessage.put("addCount", addCount);
        baseMessage.put("haveToCount", haveToCount);
        return baseMessage;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String companyLabel = "社区";
        LinkedList<Map<String, Object>> communityResidents;
        long ct = (long) (companyType == null ? 0L : companyType);
        boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getLevel().getValue()) || (systemCompanyType.equals(systemUser.getLevel().getValue()) && ct == (int) subdistrictCompanyType);
        if (companyType == null || ct == (int) systemCompanyType) {
            companyLabel = "街道";
            communityResidents = communityResidentMapper.countForGroupSubdistrict();
        } else if (isSystemRoleCount) {
            communityResidents = communityResidentMapper.countForGroupCommunity(companyId);
        } else if ((int) communityCompanyType == (long) systemUser.getLevel().getValue()) {
            communityResidents = communityResidentMapper.countForGroupByCommunityId(companyId);
        } else {
            communityResidents = new LinkedList<>();
        }
        return barChartDataHandler("社区居民总户数", companyLabel, "户", communityResidents);
    }

    /**
     * 社区居民联系方式处理
     *
     * @param sourceId   来源编号
     * @param phone1Cell 联系方式一单元格对象
     * @param phone2Cell 联系方式二单元格对象
     * @param phone3Cell 联系方式三单元格对象
     * @return 联系方式拼接字符串
     */
    private List<PhoneNumber> residentPhoneHandler(String sourceId, Cell phone1Cell, Cell phone2Cell, Cell phone3Cell) {
        String phone1 = convertCellString(phone1Cell);
        String phone2 = convertCellString(phone2Cell);
        String phone3 = convertCellString(phone3Cell);
        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhoneNumber(phone1).setSourceType(PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT).setSourceId(sourceId);
        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setPhoneNumber(phone2).setSourceType(PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT).setSourceId(sourceId);
        PhoneNumber phoneNumber3 = new PhoneNumber();
        phoneNumber3.setPhoneNumber(phone3).setSourceType(PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT).setSourceId(sourceId);
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneNumber1);
        phoneNumbers.add(phoneNumber2);
        phoneNumbers.add(phoneNumber3);
        return phoneNumbers;
    }
}
