package com.github.phonenumbermanager.service.impl;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityResidentMapper;
import com.github.phonenumbermanager.service.*;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.vo.CommunityResidentSearchVo;

import cn.hutool.core.convert.Convert;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident>
    implements CommunityResidentService {
    @Resource
    private CommunityResidentPhoneNumberService communityResidentPhoneNumberService;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private CompanyService companyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CommunityResident entity) {
        baseMapper.insert(entity);
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = communityResidentPhoneNumbersHandler(entity);
        return communityResidentPhoneNumberService.saveBatch(communityResidentPhoneNumbers);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CommunityResident entity) {
        baseMapper.updateById(entity);
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = communityResidentPhoneNumbersHandler(entity);
        return communityResidentPhoneNumberService.saveOrUpdateBatch(communityResidentPhoneNumbers);
    }

    @Override
    public CommunityResident getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Override
    public IPage<CommunityResident> page(List<Company> companies, CommunityResidentSearchVo communityResidentSearchVo,
        IPage<CommunityResident> page) {
        return baseMapper.selectBySearchVo(companies, communityResidentSearchVo, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Long streetId, Map<String, Configuration> configurationsMap) {
        List<CommunityResident> residents = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = new ArrayList<>();
        int excelCommunityCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_community_name_cell_number").getContent());
        int excelCommunityResidentNameCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_name_cell_number").getContent());
        int excelResidentAddressCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_address_cell_number").getContent());
        int excelPhone1CellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_phone1_cell_number").getContent());
        int excelPhone2CellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_phone2_cell_number").getContent());
        int excelPhone3CellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_phone3_cell_number").getContent());
        int excelSubcontractorCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_subcontractor_name_cell_number").getContent());
        List<SystemUser> systemUsers = systemUserService.listCorrelationByCompanyId(streetId);
        List<Company> companies = companyService.list(new QueryWrapper<Company>().eq("parent_id", streetId));
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
            List<PhoneNumber> phoneNumberList = phoneNumberService.list(wrapper);
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
            communityResidentPhoneNumberService.remove(communityResidentPhoneNumberWrapper);
            phoneNumberService.saveOrUpdateBatch(phoneNumbers);
            return communityResidentPhoneNumberService.saveBatch(communityResidentPhoneNumbers);

        }
        return false;
    }

    @Override
    public List<LinkedHashMap<String, Object>> listCorrelationToMap(Long companyId) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        List<Company> companies = companyService.list(new QueryWrapper<Company>().eq("parent_id", companyId));
        List<Long> companyIds = new ArrayList<>();
        Map<Long, String> companyParentName = new HashMap<>();
        companyService.listSubmissionCompanyIds(companyIds, companies, companyService.list(), companyParentName);
        List<CommunityResident> communityResidents = baseMapper.selectListByCompanyIds(companyIds);
        if (!communityResidents.isEmpty()) {
            for (CommunityResident communityResident : communityResidents) {
                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                String communityName = communityResident.getCompany().getName()
                    .replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String streetName = companyParentName.get(communityResident.getCompanyId())
                    .replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "")
                    .replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                hashMap.put("街道", streetName);
                hashMap.put("社区", communityName);
                hashMap.put("户主姓名", communityResident.getName());
                hashMap.put("家庭地址", communityResident.getAddress());
                for (int i = 0; i < communityResident.getPhoneNumbers().size(); i++) {
                    hashMap.put("电话" + (i + 1), communityResident.getPhoneNumbers().get(i));
                }
                // 处理分包人
                hashMap.put("分包人", communityName + communityResident.getSystemUser().getUsername());
                list.add(hashMap);
            }
        }
        return list;
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyService.list();
        if (companyId == null) {
            return computedBaseMessage(companies, companyAll, companyIds);
        } else {
            companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
            if (companyIds.contains(companyId)) {
                List<Company> companyList = companyService.list(new QueryWrapper<Company>().eq("id", companyId));
                return computedBaseMessage(companyList, companyAll, companyIds);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long companyId) {
        List<Map<String, Object>> communityResidents = new ArrayList<>();
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyService.list();
        if (companyId == null) {
            companyService.listSubmissionCompanyIds(companyIds, companies, companyAll, null);
            communityResidents = baseMapper.countForGroupCompany(companyIds);
        } else {
            companyService.listRecursionCompanyIds(companyIds, companies, companyAll, companyId);
            if (companyIds.contains(companyId)) {
                List<Company> companyList = companyService.list(new QueryWrapper<Company>().eq("id", companyId));
                companyIds.clear();
                companyService.listSubmissionCompanyIds(companyIds, companyList, companyAll, null);
                communityResidents = baseMapper.countForGroupCompany(companyIds);
            }
        }
        return barChartDataHandler("社区居民总户数", null, "户", communityResidents);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Long id) {
        return baseMapper.deleteById(id) > 0 && communityResidentPhoneNumberService
            .remove(new QueryWrapper<CommunityResidentPhoneNumber>().eq("community_resident_id", id));
    }

    /**
     * 保存、更新联系方式处理
     *
     * @param entity
     *            需要处理的对象
     * @return 社区居民与联系方式中间对象
     */
    private List<CommunityResidentPhoneNumber> communityResidentPhoneNumbersHandler(CommunityResident entity) {
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = new ArrayList<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> {
            PhoneNumber number = phoneNumberService
                .getOne(new QueryWrapper<PhoneNumber>().eq("phone_number", phoneNumber.getPhoneNumber()));
            long phoneNumberId;
            if (number == null) {
                phoneNumberService.save(phoneNumber);
                phoneNumberId = phoneNumber.getId();
            } else {
                phoneNumberId = number.getId();
            }
            communityResidentPhoneNumbers.add(new CommunityResidentPhoneNumber().setCommunityResidentId(entity.getId())
                .setPhoneNumberId(phoneNumberId));
        });
        return communityResidentPhoneNumbers;
    }

    private void phoneNumberHandle(String phoneNumber, Long communityResidentId, QueryWrapper<PhoneNumber> wrapper,
        List<PhoneNumber> phoneNumbers, List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers) {
        String phone = CommonUtil.replaceSpecialStr(phoneNumber);
        if (StringUtils.isNotEmpty(phone)) {
            PhoneNumber phoneNumber1 = CommonUtil.phoneNumber2Object(phone);
            phoneNumbers.add(phoneNumber1);
            communityResidentPhoneNumbers.add(new CommunityResidentPhoneNumber()
                .setCommunityResidentId(communityResidentId).setPhoneNumberId(phoneNumber1.getId()));
            wrapper.eq("phone_number", phone).or();
        }
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
        companyService.listSubmissionCompanyIds(companyIds, companies, companyAll, null);
        QueryWrapper<CommunityResident> communityResidentWrapper = new QueryWrapper<>();
        QueryWrapper<Company> companyWrapper = new QueryWrapper<>();
        companyWrapper.select("SUM(actual_number) AS actual");
        companyIds.forEach(id -> {
            communityResidentWrapper.eq("company_id", id).or();
            companyWrapper.eq("company_id", id).or();
        });
        baseMessage.put("addCount", count(communityResidentWrapper));
        baseMessage.put("haveToCount", companyService.getMap(companyWrapper).get("actual"));
        return baseMessage;
    }
}
