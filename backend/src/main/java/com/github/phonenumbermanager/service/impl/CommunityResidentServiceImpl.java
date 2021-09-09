package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityMapper;
import com.github.phonenumbermanager.mapper.CommunityResidentMapper;
import com.github.phonenumbermanager.mapper.SubcontractorMapper;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.PhoneNumberService;

import cn.hutool.core.convert.Convert;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident>
    implements CommunityResidentService {
    // private final Pattern communityPattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");
    @Resource
    private CommunityMapper communityMapper;
    @Resource
    private SubcontractorMapper subcontractorMapper;
    @Resource
    private PhoneNumberService phoneNumberService;

    @Override
    public CommunityResident getCorrelation(Serializable id) {
        return baseMapper.selectAndCommunityById(id);
    }

    @Override
    public IPage<CommunityResident> get(SystemUser systemUser, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType, CommunityResident communityResident,
        Serializable companyId, Serializable companyType, Integer pageNumber, Integer pageDataSize) {
        Map<String, Object> company = getCompany(systemUser, companyId, companyType);
        Page<CommunityResident> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectByUserRole(page, (Serializable)company.get("companyType"),
            (Serializable)company.get("companyId"), systemCompanyType, communityCompanyType, subdistrictCompanyType,
            communityResident);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap) {
        List<CommunityResident> residents = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        int excelCommunityCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_community_name_cell_number"));
        int excelCommunityResidentNameCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_name_cell_number"));
        int excelResidentAddressCellNumber = Convert.toInt(configurationsMap.get("excel_resident_address_cell_number"));
        int excelPhone1CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone1_cell_number"));
        int excelPhone2CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone2_cell_number"));
        int excelPhone3CellNumber = Convert.toInt(configurationsMap.get("excel_resident_phone3_cell_number"));
        int excelSubcontractorCellNumber =
            Convert.toInt(configurationsMap.get("excel_resident_subcontractor_name_cell_number"));
        List<Subcontractor> subcontractors = subcontractorMapper.selectBySubdistrictId(subdistrictId);
        List<Community> communities = communityMapper.selectBySubdistrictId(subdistrictId);
        for (List<Object> datum : data) {
            CommunityResident communityResident = new CommunityResident();
            communityResident.setId(IdWorker.getId())
                .setName(String.valueOf(datum.get(excelCommunityResidentNameCellNumber)))
                .setAddress(String.valueOf(datum.get(excelResidentAddressCellNumber)));
            List<Community> communityList = communities.stream()
                .filter(c -> String.valueOf(datum.get(excelCommunityCellNumber)).contains(c.getName()))
                .collect(Collectors.toList());
            if (communityList.size() == 0) {
                throw new BusinessException("未找到对应的社区，请重试！");
            }
            communityResident.setCommunityId(communityList.get(0).getId());
            List<Subcontractor> subcontractorList = subcontractors.stream()
                .filter(s -> String.valueOf(datum.get(excelSubcontractorCellNumber)).equals(s.getName()))
                .collect(Collectors.toList());
            if (subcontractorList.size() == 0) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            communityResident.setSubcontractorId(subcontractorList.get(0).getId());
            // 联系方式
            PhoneNumber phoneNumber1 = phoneHandler(String.valueOf(datum.get(excelPhone1CellNumber)));
            if (phoneNumber1 != null) {
                phoneNumber1.setSourceId(String.valueOf(communityResident.getId()));
                phoneNumbers.add(phoneNumber1);
            }
            PhoneNumber phoneNumber2 = phoneHandler(String.valueOf(datum.get(excelPhone2CellNumber)));
            if (phoneNumber2 != null) {
                phoneNumber2.setSourceId(String.valueOf(communityResident.getId()));
                phoneNumbers.add(phoneNumber2);
            }
            PhoneNumber phoneNumber3 = phoneHandler(String.valueOf(datum.get(excelPhone3CellNumber)));
            if (phoneNumber3 != null) {
                phoneNumber3.setSourceId(String.valueOf(communityResident.getId()));
                phoneNumbers.add(phoneNumber3);
            }
            residents.add(communityResident);
        }
        if (residents.size() > 0) {
            QueryWrapper<CommunityResident> wrapper = new QueryWrapper<>();
            wrapper.eq("subdistrict_id", subdistrictId);
            baseMapper.delete(wrapper);
            phoneNumberService.saveBatch(phoneNumbers);
            return saveBatch(residents);
        }
        return false;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getCorrelation(Serializable communityCompanyType,
        Serializable subdistrictCompanyType, List<Map<String, Object>> userData) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        List<CommunityResident> communityResidents =
            baseMapper.selectByUserData(userData, communityCompanyType, subdistrictCompanyType);
        if (communityResidents != null && communityResidents.size() > 0) {
            for (CommunityResident communityResident : communityResidents) {
                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                String communityName = communityResident.getCommunity().getName()
                    .replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getName()
                    .replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "")
                    .replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
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

    @Override
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Long addCount = null;
        Long haveToCount = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemCompanyType);
        long ct = (long)(companyType == null ? 0L : companyType);
        int cct = (int)communityCompanyType;
        int sct = (int)subdistrictCompanyType;
        if (isSystemRole) {
            addCount = (long)count();
            haveToCount = communityMapper.sumActualNumber();
        } else if (ct == cct) {
            addCount = baseMapper.countByCommunityId(companyId);
            haveToCount = communityMapper.selectActualNumberById(companyId);
        } else if (ct == sct) {
            addCount = baseMapper.countBySubdistrictId(companyId);
            haveToCount = communityMapper.sumActualNumberBySubdistrictId(companyId);
        }
        baseMessage.put("addCount", addCount);
        baseMessage.put("haveToCount", haveToCount);
        return baseMessage;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String companyLabel = "社区";
        LinkedList<Map<String, Object>> communityResidents;
        long ct = (long)(companyType == null ? 0L : companyType);
        boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getLevel().getValue())
            || (systemCompanyType.equals(systemUser.getLevel().getValue()) && ct == (int)subdistrictCompanyType);
        if (companyType == null || ct == (int)systemCompanyType) {
            companyLabel = "街道";
            communityResidents = baseMapper.countForGroupSubdistrict();
        } else if (isSystemRoleCount) {
            communityResidents = baseMapper.countForGroupCommunity(companyId);
        } else if ((int)communityCompanyType == (long)systemUser.getLevel().getValue()) {
            communityResidents = baseMapper.countForGroupByCommunityId(companyId);
        } else {
            communityResidents = new LinkedList<>();
        }
        return barChartDataHandler("社区居民总户数", companyLabel, "户", communityResidents);
    }
}
