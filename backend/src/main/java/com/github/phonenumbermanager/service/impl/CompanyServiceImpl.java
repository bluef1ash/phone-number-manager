package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.PhoneNumberService;

/**
 * 单位业务实现
 *
 * @author 廿二月的天
 */
@Service("companyService")
public class CompanyServiceImpl extends BaseServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private PhoneNumberService phoneNumberService;

    @Override
    public Company getCorrelation(Serializable id) {
        SystemUser systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return baseMapper.selectCorrelationById(id);
    }

    @Override
    public IPage<Company> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Page<Company> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectCorrelation(page);
    }

    @Override
    public List<Community> getCorrelation() {
        return baseMapper.selectAndSubdistrictAll();
    }

    @Override
    public List<Community> get(SystemUser systemUser, Serializable communityCompanyType,
        Serializable subdistrictCompanyType) {
        List<Community> communities = null;
        // TODO: 2021/9/12 0012 用户权限
        // if (communityCompanyType.equals(systemUser.getLevel().getValue())) {
        // communities = new ArrayList<>();
        // Community community = baseMapper.selectCorrelationById(systemUser.getCompanyId());
        // communities.add(community);
        // } else if (subdistrictCompanyType.equals(systemUser.getLevel().getValue())) {
        // communities = baseMapper.selectCorrelationBySubdistrictId(systemUser.getCompanyId());
        // } else {
        // communities = baseMapper.selectCorrelationSubdistrictsAll();
        // }
        return communities;
    }

    @Override
    public List<Community> getBySubdistrictId(Serializable subdistrictId) {
        return baseMapper.selectBySubdistrictId(subdistrictId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(List<Map<String, Object>> data, Integer changeType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType) {
        Community community = new Community();
        if (changeType == 0) {
            community.setResidentSubmitted(true);
        } else {
            community.setDormitorySubmitted(true);
        }
        UpdateWrapper<Community> wrapper = new UpdateWrapper<>();
        for (Map<String, Object> item : data) {
            if (communityCompanyType.equals(item.get("companyType"))) {
                wrapper.eq("id", item.get("companyId"));
            } else if (subdistrictCompanyType.equals(item.get("companyType"))) {
                wrapper.eq("subdistrict_id", item.get("companyType"));
            }
        }
        return baseMapper.update(community, wrapper) > 0;
    }

    @Override
    public boolean isSubmittedById(Integer submitType, Serializable id) {
        return baseMapper.selectSubmittedById(submitType, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Serializable id) {
        int communityResidentCount =
            communityResidentService.count(new QueryWrapper<CommunityResident>().eq("company_id", id));
        if (communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        int dormitoryManagerCount =
            dormitoryManagerService.count(new QueryWrapper<DormitoryManager>().eq("company_id", id));
        if (dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        return baseMapper.deleteById(id) > 0
            && phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.COMMUNITY, id);
    }
}
