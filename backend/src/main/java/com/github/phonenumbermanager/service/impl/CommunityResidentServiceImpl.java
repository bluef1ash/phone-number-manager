package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.CommunityResidentService;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.AllArgsConstructor;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident>
    implements CommunityResidentService {
    private final CommunityResidentPhoneNumberMapper communityResidentPhoneNumberMapper;
    private final SystemUserMapper systemUserMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final CompanyMapper companyMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CommunityResident entity) {
        entity.setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSystemUserId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        boolean isSuccess = baseMapper.insert(entity) > 0;
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        communityResidentPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CommunityResident entity) {
        entity.setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSystemUserId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        if (phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers()) > 0) {
            communityResidentPhoneNumberMapper.delete(new LambdaQueryWrapper<CommunityResidentPhoneNumber>()
                .eq(CommunityResidentPhoneNumber::getCommunityResidentId, entity.getId()));
            communityResidentPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        }
        return isSuccess;
    }

    @Override
    public CommunityResident getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Override
    public IPage<CommunityResident> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        if (search != null && !search.isEmpty()) {
            JSONObject systemUser = search.get("systemUser", JSONObject.class, true);
            if (systemUser != null && !systemUser.isEmpty()) {
                JSONArray username = systemUser.get("username", JSONArray.class);
                List<Long> ids =
                    username.toList(Long[].class).stream().map(id -> id[id.length - 1]).collect(Collectors.toList());
                search.set("systemUser", ids);
            }
        }
        return baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize), search, sort);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Map<String, JSONObject> configurationMap) {
        /*List<CommunityResident> residents = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = new ArrayList<>();
        int excelCommunityCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_community_name_cell_number").get("content"));
        int excelCommunityResidentNameCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_name_cell_number").get("content"));
        int excelResidentAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_address_cell_number").get("content"));
        int excelPhone1CellNumber =
            Convert.toInt(configurationMap.get("excel_resident_phone1_cell_number").get("content"));
        int excelPhone2CellNumber =
            Convert.toInt(configurationMap.get("excel_resident_phone2_cell_number").get("content"));
        int excelPhone3CellNumber =
            Convert.toInt(configurationMap.get("excel_resident_phone3_cell_number").get("content"));
        int excelSubcontractorCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_subcontractor_name_cell_number").get("content"));
        //List<SystemUser> systemUsers = systemUserMapper.selectByCompanyId(streetId);
        //List<Company> companies = companyMapper.selectList(new QueryWrapper<Company>().eq("parent_id", streetId));
        for (List<Object> datum : data) {
            CommunityResident communityResident = new CommunityResident();
            communityResident.setId(IdWorker.getId())
                .setName(String.valueOf(datum.get(excelCommunityResidentNameCellNumber)))
                .setAddress(String.valueOf(datum.get(excelResidentAddressCellNumber)));
            Optional<Company> company = companies.stream()
                .filter(c -> String.valueOf(datum.get(excelCommunityCellNumber)).contains(c.getName())).findFirst();
            if (company.isEmpty()) {
                throw new BusinessException("未找到对应的社区，请重试！");
            }
            communityResident.setCompanyId(company.get().getId());
            Optional<SystemUser> systemUser = systemUsers.stream()
                .filter(s -> String.valueOf(datum.get(excelSubcontractorCellNumber)).equals(s.getUsername()))
                .findFirst();
            if (systemUser.isEmpty()) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            communityResident.setSystemUserId(systemUser.get().getId());
            // 联系方式
            QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
            phoneNumberHandle(String.valueOf(datum.get(excelPhone1CellNumber)), communityResident.getId(), wrapper,
                phoneNumbers, communityResidentPhoneNumbers);
            phoneNumberHandle(String.valueOf(datum.get(excelPhone2CellNumber)), communityResident.getId(), wrapper,
                phoneNumbers, communityResidentPhoneNumbers);
            phoneNumberHandle(String.valueOf(datum.get(excelPhone3CellNumber)), communityResident.getId(), wrapper,
                phoneNumbers, communityResidentPhoneNumbers);
            List<PhoneNumber> phoneNumberList = phoneNumberMapper.selectList(wrapper);
            if (!phoneNumberList.isEmpty()) {
                if (phoneNumberList.size() == phoneNumbers.size()) {
                    phoneNumbers = phoneNumberList;
                } else {
                    phoneNumbers.forEach(phoneNumber -> phoneNumberList.forEach(phoneNumber1 -> {
                        if (phoneNumber.getPhoneNumber().equals(phoneNumber1.getPhoneNumber())) {
                            phoneNumber.setId(phoneNumber1.getId());
                        }
                    }));
                }
                List<PhoneNumber> finalPhoneNumbers = phoneNumbers;
                communityResidentPhoneNumbers.forEach(communityResidentPhoneNumber -> finalPhoneNumbers
                    .forEach(phoneNumber -> communityResidentPhoneNumber.setPhoneNumberId(phoneNumber.getId())));
            }
            residents.add(communityResident);
        }
        if (residents.size() > 0) {
            QueryWrapper<CommunityResident> communityResidentWrapper = new QueryWrapper<>();
            QueryWrapper<CommunityResidentPhoneNumber> communityResidentPhoneNumberWrapper = new QueryWrapper<>();
            companies.forEach(company -> communityResidentWrapper.eq("company_id", company.getId()).or());
            baseMapper.delete(communityResidentWrapper);
            super.saveBatch(residents);
            residents.forEach(communityResident -> communityResidentPhoneNumberWrapper
                .eq("community_resident_id", communityResident.getId()).or());
            communityResidentPhoneNumberMapper.delete(communityResidentPhoneNumberWrapper);
            phoneNumberMapper.insertBatchSomeColumn(phoneNumbers);
            return communityResidentPhoneNumberMapper.insertBatchSomeColumn(communityResidentPhoneNumbers) > 0;

        }*/
        return false;
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(List<Company> companies, Map<String, JSONObject> configurationMap) {
        String excelResidentTitleUp = Convert.toStr(configurationMap.get("excel_resident_title_up").get("content"));
        String excelResidentTitle = Convert.toStr(configurationMap.get("excel_resident_title").get("content"));
        List<Company> companyAll = companyMapper.selectList(null);
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        SystemUser principal = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (systemAdministratorId.equals(principal.getId()) && companies == null) {
            companies = companyAll.stream().filter(company -> company.getParentId() == 0L).collect(Collectors.toList());
        }
        List<Long> companyIds = getSubordinateCompanyIds(companies, companyAll);
        List<CommunityResident> communityResidents = baseMapper.selectListByCompanyIds(companyIds);
        if (communityResidents.isEmpty()) {
            return null;
        }
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (CommunityResident communityResident : communityResidents) {
            LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
            Optional<Company> companyParentName = companies.stream()
                .filter(company -> company.getId().equals(communityResident.getCompany().getParentId())).findFirst();
            if (companyParentName.isPresent()) {
                String streetName =
                    companyParentName.get().getName().replaceAll(SystemConstant.STREET_NAME_PATTERN, "");
                hashMap.put("街道", streetName);
            }
            String communityName =
                communityResident.getCompany().getName().replaceAll(SystemConstant.COMMUNITY_NAME_PATTERN, "");
            hashMap.put("社区", communityName);
            hashMap.put("户主姓名", communityResident.getName());
            hashMap.put("家庭地址", communityResident.getAddress());
            for (int i = 0; i < communityResident.getPhoneNumbers().size(); i++) {
                hashMap.put("电话" + (i + 1), communityResident.getPhoneNumbers().get(i).getPhoneNumber());
            }
            if (communityResident.getPhoneNumbers().size() == 1) {
                hashMap.put("电话2", "");
                hashMap.put("电话3", "");
            } else {
                hashMap.put("电话3", "");
            }
            // 处理分包人
            hashMap.put("分包人", communityName + communityResident.getSystemUser().getUsername());
            list.add(hashMap);
        }
        ExcelWriter excelWriter = ExcelUtil.getBigWriter();
        SXSSFSheet sheet = (SXSSFSheet)excelWriter.getSheet();
        CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
        setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
        excelWriter.writeCellValue(0, 0, excelResidentTitleUp);
        excelWriter.setStyle(firstRowStyle, 0, 0);
        excelWriter.passCurrentRow();
        CellStyle titleStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
        setCellStyle(titleStyle, excelWriter, "方正小标宋简体", (short)16, false, false, false);
        excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, list.get(0).keySet().size(),
            excelResidentTitle, titleStyle);
        excelWriter.passCurrentRow();
        CellStyle dateRowStyle = excelWriter.getOrCreateCellStyle(6, excelWriter.getCurrentRow());
        setCellStyle(dateRowStyle, excelWriter, "宋体", (short)11, false, false, true);
        excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 6, 7,
            "时间：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")), dateRowStyle);
        excelWriter.passCurrentRow();
        StyleSet styleSet = excelWriter.getStyleSet();
        CellStyle headCellStyle = styleSet.getHeadCellStyle();
        setCellStyle(headCellStyle, excelWriter, "黑体", (short)12, false, true, false);
        CellStyle cellStyle = styleSet.getCellStyle();
        setCellStyle(cellStyle, excelWriter, "仿宋_GB2312", (short)9, false, true, true);
        excelWriter.write(list, true);
        sheet.trackAllColumnsForAutoSizing();
        excelWriter.autoSizeColumnAll();
        return excelWriter;
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyMapper.selectList(null);
        if (companyId == null) {
            return computedBaseMessage(companies, companyAll, companyIds);
        } else {
            // companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
            if (companyIds.contains(companyId)) {
                List<Company> companyList = companyMapper.selectList(new QueryWrapper<Company>().eq("id", companyId));
                return computedBaseMessage(companyList, companyAll, companyIds);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long companyId) {
        List<Map<String, Object>> communityResidents = new ArrayList<>();
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyMapper.selectList(null);
        if (companyId == null) {
            // companyService.listSubmissionCompanyIds(companyIds, companies, companyAll, 0, null);
            // communityResidents = baseMapper.countForGroupCompany(companyIds);
        } else {
            // companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
            if (companyIds.contains(companyId)) {
                List<Company> companyList = companyMapper.selectList(new QueryWrapper<Company>().eq("id", companyId));
                companyIds.clear();
                // companyService.listSubmissionCompanyIds(companyIds, companyList, companyAll, 0, null);
                // communityResidents = baseMapper.countForGroupCompany(companyIds);
            }
        }
        return barChartDataHandler("社区居民总户数", null, "户", communityResidents);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0 && communityResidentPhoneNumberMapper
            .delete(new QueryWrapper<CommunityResidentPhoneNumber>().eq("community_resident_id", id)) > 0;
    }

    @Override
    public List<CommunityResident> listByPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        return baseMapper.selectCorrelationByPhoneNumbers(phoneNumbers);
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
    private Map<String, Object> computedBaseMessage(List<Company> companies, List<Company> companyAll,
        List<Long> companyIds) {
        Map<String, Object> baseMessage = new HashMap<>(2);
        // companyService.listSubmissionCompanyIds(companyIds, companies, companyAll, 0, null);
        QueryWrapper<CommunityResident> communityResidentWrapper = new QueryWrapper<>();
        QueryWrapper<Company> companyWrapper = new QueryWrapper<>();
        companyWrapper.select("SUM(actual_number) AS actual");
        companyIds.forEach(id -> {
            communityResidentWrapper.eq("company_id", id).or();
            companyWrapper.eq("company_id", id).or();
        });
        baseMessage.put("addCount", count(communityResidentWrapper));
        // baseMessage.put("haveToCount", companyMapper.select(companyWrapper).get("actual"));
        return baseMessage;
    }

    /**
     * 处理社区居民联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<CommunityResidentPhoneNumber> phoneNumbersHandler(CommunityResident entity) {
        QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> wrapper.eq("phone_number", phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(phoneNumber -> new CommunityResidentPhoneNumber()
            .setCommunityResidentId(entity.getId()).setPhoneNumberId(phoneNumber.getId())).collect(Collectors.toList());
    }
}
