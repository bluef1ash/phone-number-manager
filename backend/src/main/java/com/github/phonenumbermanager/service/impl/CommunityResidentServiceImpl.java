package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;

/**
 * 社区居民信息业务实现
 *
 * @author 廿二月的天
 */
@Service
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident>
    implements CommunityResidentService {
    private final CommunityResidentPhoneNumberMapper communityResidentPhoneNumberMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final CompanyMapper companyMapper;
    private final CompanyExtraMapper companyExtraMapper;
    private final SubcontractorMapper subcontractorMapper;
    private int excelCommunityCellNumber;
    private int excelCommunityResidentNameCellNumber;
    private int excelResidentAddressCellNumber;
    private int excelPhone1CellNumber;
    private int excelPhone2CellNumber;
    private int excelPhone3CellNumber;
    private int excelSubcontractorCellNumber;

    @Autowired
    public CommunityResidentServiceImpl(CommunityResidentPhoneNumberMapper communityResidentPhoneNumberMapper,
        PhoneNumberMapper phoneNumberMapper, CompanyMapper companyMapper, CompanyExtraMapper companyExtraMapper,
        SubcontractorMapper subcontractorMapper) {
        this.communityResidentPhoneNumberMapper = communityResidentPhoneNumberMapper;
        this.phoneNumberMapper = phoneNumberMapper;
        this.companyMapper = companyMapper;
        this.companyExtraMapper = companyExtraMapper;
        this.subcontractorMapper = subcontractorMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CommunityResident entity) {
        boolean isSuccess = baseMapper.insert(entity) > 0;
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        communityResidentPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CommunityResident entity) {
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
        CommunityResident communityResident = baseMapper.selectAndCompanyById(id);
        List<String> info = new ArrayList<>();
        info.add(communityResident.getCompany().getName());
        info.add(communityResident.getSubcontractor().getName());
        communityResident.setSubcontractorInfo(info);
        return communityResident;
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
        Page<CommunityResident> page = new Page<>(pageNumber, pageDataSize);
        page.setSearchCount(false);
        page.setTotal(baseMapper.selectCorrelationCountByCompanies(companies, search, sort));
        return baseMapper.selectCorrelationByCompanies(page, companies, search, sort);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Map<String, JSONObject> configurationMap) {
        getUploadExcelVariable(configurationMap);
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = new ArrayList<>();
        List<CommunityResident> residents = importExcelDataHandle(data, phoneNumbers, communityResidentPhoneNumbers);
        if (residents.size() > 0) {
            QueryWrapper<CommunityResidentPhoneNumber> communityResidentPhoneNumberWrapper = new QueryWrapper<>();
            boolean isSuccess = baseMapper.insertBatchSomeColumn(residents) > 0;
            residents.forEach(communityResident -> communityResidentPhoneNumberWrapper
                .eq("community_resident_id", communityResident.getId()).or());
            communityResidentPhoneNumberMapper.delete(communityResidentPhoneNumberWrapper);
            phoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbers);
            communityResidentPhoneNumberMapper.insertBatchSomeColumn(communityResidentPhoneNumbers);
            return isSuccess;
        }
        return false;
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(SystemUser currentSystemUser,
        Map<String, JSONObject> configurationMap) {
        String excelResidentTitleUp = Convert.toStr(configurationMap.get("excel_resident_title_up").get("content"));
        String excelResidentTitle = Convert.toStr(configurationMap.get("excel_resident_title").get("content"));
        List<Company> companyAll = companyMapper.selectList(null);
        List<Long> subordinateCompanyIds =
            exportExcelGetSubordinateCompanyIds(currentSystemUser, configurationMap, companyAll);
        List<CommunityResident> communityResidents = baseMapper.selectListByCompanyIds(subordinateCompanyIds);
        if (communityResidents.isEmpty()) {
            return null;
        }
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        int tableHeaderCount = 9;
        for (CommunityResident communityResident : communityResidents) {
            LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>(tableHeaderCount);
            Optional<Company> companyParentName = companyAll.stream()
                .filter(company -> company.getId().equals(communityResident.getCompany().getParentId())).findFirst();
            String streetName = "";
            if (companyParentName.isPresent()) {
                streetName = companyParentName.get().getName().replaceAll(SystemConstant.STREET_NAME_PATTERN, "");
            }
            hashMap.put("街道", streetName);
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
            hashMap.put("分包人", communityName + communityResident.getSubcontractor().getName());
            list.add(hashMap);
        }
        ExcelWriter excelWriter = ExcelUtil.getBigWriter();
        SXSSFSheet sheet = (SXSSFSheet)excelWriter.getSheet();
        CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
        setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
        excelWriter.writeCellValue(0, 0, excelResidentTitleUp);
        excelWriter.setStyle(firstRowStyle, 0, 0);
        exportExcelTitleHandle(excelWriter, excelResidentTitle, list.get(0).keySet().size());
        CellStyle dateRowStyle = excelWriter.getOrCreateCellStyle(6, excelWriter.getCurrentRow());
        setCellStyle(dateRowStyle, excelWriter, "宋体", (short)11, false, false, false);
        excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 6, 7,
            "时间：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")), dateRowStyle);
        excelWriter.passCurrentRow();
        StyleSet styleSet = excelWriter.getStyleSet();
        CellStyle headCellStyle = styleSet.getHeadCellStyle();
        setCellStyle(headCellStyle, excelWriter, "黑体", (short)12, false, true, false);
        CellStyle cellStyle = styleSet.getCellStyle();
        setCellStyle(cellStyle, excelWriter, "仿宋_GB2312", (short)9, false, true, false);
        sheet.setRandomAccessWindowSize(-1);
        excelWriter.write(list, true);
        for (int i = 0; i < tableHeaderCount; ++i) {
            excelWriter.autoSizeColumn(i);
        }
        return excelWriter;
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long[] companyIds) {
        List<Company> companyAll = companyMapper.selectList(null);
        Long[] systemUserCompanyParentIds = frontendUserRequestCompanyHandle(companies, companyAll, companyIds);
        return computedBaseMessage(systemUserCompanyParentIds, companyAll);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0 && communityResidentPhoneNumberMapper
            .delete(new QueryWrapper<CommunityResidentPhoneNumber>().eq("community_resident_id", id)) > 0;
    }

    @Override
    public List<CommunityResident> listByPhoneNumbers(Long id, List<PhoneNumber> phoneNumbers) {
        return baseMapper.selectCorrelationByPhoneNumbers(id, phoneNumbers);
    }

    /**
     * 计算录入统计信息
     *
     * @param companyIds
     *            需要获取的单位编号集合
     * @param companyAll
     *            所有单位集合
     * @return 录入统计信息
     */
    private Map<String, Object> computedBaseMessage(Long[] companyIds, List<Company> companyAll) {
        Map<String, Object> baseMessage = new HashMap<>(2);
        LambdaQueryWrapper<CommunityResident> communityResidentWrapper = new LambdaQueryWrapper<>();
        QueryWrapper<CompanyExtra> companyExtraWrapper = new QueryWrapper<>();
        Arrays.stream(companyIds).forEach(companyId -> {
            List<Company> companyList = CommonUtil.listRecursionCompanies(companyAll, companyId);
            if (companyList.isEmpty()) {
                baseMessageQueryWrapperHandle(communityResidentWrapper, companyExtraWrapper, companyId);
            } else {
                companyList.forEach(company -> baseMessageQueryWrapperHandle(communityResidentWrapper,
                    companyExtraWrapper, company.getId()));
            }
        });
        companyExtraWrapper.select("IFNULL(SUM(name), 0) AS 'needTo'");
        baseMessage.put("inputCount", baseMapper.selectCount(communityResidentWrapper));
        List<Map<String, Object>> companyExtraMaps = companyExtraMapper.selectMaps(companyExtraWrapper);
        double haveToCount = 0;
        if (!companyExtraMaps.isEmpty()) {
            haveToCount = (double)companyExtraMaps.get(0).get("needTo");
        }
        baseMessage.put("haveToCount", haveToCount);
        baseMessage.put("loading", false);
        return baseMessage;
    }

    /**
     * 计算录入统计信息查询对象处理
     *
     * @param communityResidentWrapper
     *            社区居民查询对象
     * @param companyExtraWrapper
     *            单位额外查询对象
     * @param companyId
     *            单位编号
     */
    private void baseMessageQueryWrapperHandle(LambdaQueryWrapper<CommunityResident> communityResidentWrapper,
        QueryWrapper<CompanyExtra> companyExtraWrapper, Long companyId) {
        communityResidentWrapper.eq(CommunityResident::getCompanyId, companyId).or();
        companyExtraWrapper.and(wrapper -> wrapper.and(w -> w.eq("company_id", companyId)).eq("name", "haveToCount"))
            .or();
    }

    /**
     * 获取上传Excel的变量
     *
     * @param configurationMap
     *            配置项
     */
    private void getUploadExcelVariable(Map<String, JSONObject> configurationMap) {
        excelCommunityCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_community_name_cell_number").get("content"));
        excelCommunityResidentNameCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_name_cell_number").get("content"));
        excelResidentAddressCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_address_cell_number").get("content"));
        excelPhone1CellNumber = Convert.toInt(configurationMap.get("excel_resident_phone1_cell_number").get("content"));
        excelPhone2CellNumber = Convert.toInt(configurationMap.get("excel_resident_phone2_cell_number").get("content"));
        excelPhone3CellNumber = Convert.toInt(configurationMap.get("excel_resident_phone3_cell_number").get("content"));
        excelSubcontractorCellNumber =
            Convert.toInt(configurationMap.get("excel_resident_subcontractor_name_cell_number").get("content"));
    }

    /**
     * 导入Excel文件数据处理
     *
     * @param data
     *            文件内数据
     * @param phoneNumbers
     *            需要加入的联系方式集合
     * @param communityResidentPhoneNumbers
     *            需要导入的关联集合
     * @return 处理完成后社区居民信息集合
     */
    private List<CommunityResident> importExcelDataHandle(List<List<Object>> data, List<PhoneNumber> phoneNumbers,
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers) {
        List<CommunityResident> residents = new ArrayList<>();
        List<Subcontractor> subcontractors = subcontractorMapper.selectList(null);
        List<PhoneNumber> phoneNumbersAll = phoneNumberMapper.selectList(null);
        List<Long> phoneNumberIds = communityResidentPhoneNumberMapper.selectList(null).stream()
            .map(CommunityResidentPhoneNumber::getPhoneNumberId).collect(Collectors.toList());
        List<Company> companies = companyMapper.selectList(null);
        for (List<Object> datum : data) {
            CommunityResident communityResident = new CommunityResident();
            String companyName = StrUtil.cleanBlank(String.valueOf(datum.get(excelCommunityCellNumber)));
            Optional<Company> company = companies.stream().filter(c -> c.getName().contains(companyName)).findFirst();
            if (company.isEmpty()) {
                throw new BusinessException("未找到对应的" + companyName + "单位读取失败！");
            }
            String subcontractorName =
                StrUtil.cleanBlank(String.valueOf(datum.get(excelSubcontractorCellNumber))).replace(companyName, "");
            Optional<Subcontractor> subcontractor = subcontractors.stream()
                .filter(s -> subcontractorName.equals(s.getName()) && s.getCompanyId().equals(company.get().getId()))
                .findFirst();
            if (subcontractor.isEmpty()) {
                throw new BusinessException("未找到对应的分包人" + subcontractorName + "的信息，请重试！");
            }
            communityResident.setId(IdWorker.getId())
                .setName(StrUtil.cleanBlank(String.valueOf(datum.get(excelCommunityResidentNameCellNumber))))
                .setAddress(StrUtil.cleanBlank(String.valueOf(datum.get(excelResidentAddressCellNumber))))
                .setCompanyId(company.get().getId()).setSubcontractorId(subcontractor.get().getId());
            // 联系方式
            String phoneNumber1 = StrUtil.cleanBlank(String.valueOf(datum.get(excelPhone1CellNumber)));
            String phoneNumber2 = StrUtil.cleanBlank(String.valueOf(datum.get(excelPhone2CellNumber)));
            String phoneNumber3 = StrUtil.cleanBlank(String.valueOf(datum.get(excelPhone3CellNumber)));
            List<PhoneNumber> phoneNumbersSource =
                CommonUtil.phoneNumber2List(phoneNumber1, phoneNumber2, phoneNumber3);
            List<PhoneNumber> phoneNumberList = phoneNumbersAll.stream()
                .filter(phoneNumber -> phoneNumber1.equals(phoneNumber.getPhoneNumber())
                    || phoneNumber2.equals(phoneNumber.getPhoneNumber())
                    || phoneNumber3.equals(phoneNumber.getPhoneNumber()))
                .collect(Collectors.toList());
            if (!phoneNumberList.isEmpty()) {
                Optional<PhoneNumber> numberOptional = phoneNumberList.stream()
                    .filter(phoneNumber -> phoneNumberIds.contains(phoneNumber.getId())).findFirst();
                if (numberOptional.isPresent()) {
                    throw new BusinessException("导入数据时有重复数据：" + numberOptional.get().getPhoneNumber() + "，请检查后再次导入！");
                }
                if (phoneNumberList.size() == phoneNumbersSource.size()) {
                    phoneNumbers.addAll(phoneNumberList);
                    List<CommunityResidentPhoneNumber> communityResidentPhoneNumberList = phoneNumberList.stream()
                        .map(phoneNumber -> new CommunityResidentPhoneNumber()
                            .setCommunityResidentId(communityResident.getId()).setPhoneNumberId(phoneNumber.getId()))
                        .collect(Collectors.toList());
                    communityResidentPhoneNumbers.addAll(communityResidentPhoneNumberList);
                } else {
                    for (PhoneNumber phoneNumber : phoneNumbersSource) {
                        CommunityResidentPhoneNumber communityResidentPhoneNumber = new CommunityResidentPhoneNumber();
                        communityResidentPhoneNumber.setCommunityResidentId(communityResident.getId())
                            .setPhoneNumberId(phoneNumber.getId());
                        phoneNumbers.add(phoneNumber);
                        for (PhoneNumber number : phoneNumberList) {
                            if (phoneNumber.getPhoneNumber().equals(number.getPhoneNumber())) {
                                communityResidentPhoneNumber.setPhoneNumberId(number.getId());
                                communityResidentPhoneNumbers.add(communityResidentPhoneNumber);
                            }
                        }
                    }
                }
            } else {
                phoneNumbers.addAll(phoneNumbersSource);
                List<CommunityResidentPhoneNumber> communityResidentPhoneNumberList = phoneNumbersSource.stream()
                    .map(phoneNumber -> new CommunityResidentPhoneNumber()
                        .setCommunityResidentId(communityResident.getId()).setPhoneNumberId(phoneNumber.getId()))
                    .collect(Collectors.toList());
                communityResidentPhoneNumbers.addAll(communityResidentPhoneNumberList);
            }
            residents.add(communityResident);
        }
        return residents;
    }

    /**
     * 处理社区居民联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<CommunityResidentPhoneNumber> phoneNumbersHandler(CommunityResident entity) {
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PhoneNumber::getPhoneNumber, entity.getPhoneNumbers());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(phoneNumber -> new CommunityResidentPhoneNumber()
            .setCommunityResidentId(entity.getId()).setPhoneNumberId(phoneNumber.getId())).collect(Collectors.toList());
    }
}
