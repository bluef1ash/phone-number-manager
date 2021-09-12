package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.UserLevelEnum;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityResidentMapper;
import com.github.phonenumbermanager.mapper.DormitoryManagerMapper;
import com.github.phonenumbermanager.mapper.SubcontractorMapper;
import com.github.phonenumbermanager.service.SubcontractorService;

/**
 * 社区分包人业务实现
 *
 * @author 廿二月的天
 */
@Service("subcontractorService")
public class SubcontractorServiceImpl extends BaseServiceImpl<SubcontractorMapper, Subcontractor>
    implements SubcontractorService {
    @Resource
    private CommunityResidentMapper communityResidentMapper;
    @Resource
    private DormitoryManagerMapper dormitoryManagerMapper;

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        String companyLabel = "社区";
        String label = null;
        String formatter = "人";
        LinkedList<Map<String, Object>> subcontractors;
        long ct = (long)(companyType == null ? 0L : companyType);
        //// TODO: 2021/9/12 0012 用户权限
        // boolean isSystemRoleCount = subdistrictCompanyType.equals(systemUser.getLevel().getValue())
        // || (systemCompanyType.equals(systemUser.getLevel().getValue()) && ct == (int)subdistrictCompanyType);
        if (companyType == null || ct == (int)systemCompanyType) {
            companyLabel = "街道";
            label = "街道分包人总人数";
            subcontractors = baseMapper.countForGroupSubdistrict();
            // } else if (isSystemRoleCount) {
            // label = "社区分包人总人数";
            // subcontractors = baseMapper.countForGroupCommunity(companyId);
            // } else if (ct == (int)communityCompanyType
            // || (int)communityCompanyType == (long)systemUser.getLevel().getValue()) {
            // label = "社区居民分包总户数";
            // formatter = "户";
            // subcontractors = baseMapper.countForGroupByCommunityId(companyId);
        } else {
            subcontractors = new LinkedList<>();
        }
        return barChartDataHandler(label, companyLabel, formatter, subcontractors);
    }

    @Override
    public IPage<Subcontractor> get(Integer pageNumber, Integer pageDataSize, UserLevelEnum userLevel,
        Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType) {
        Page<Subcontractor> page = new Page<>(pageNumber, pageDataSize);
        IPage<Subcontractor> subcontractors = null;
        if (communityCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.select(page, companyId);
        } else if (subdistrictCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectBySubdistrictId(page, companyId);
        } else if (systemCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectCorrelationAll(page);
        }
        return subcontractors;
    }

    @Override
    public List<Subcontractor> get(UserLevelEnum userLevel, Serializable companyId, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        List<Subcontractor> subcontractors = null;
        if (communityCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.select(companyId);
        } else if (subdistrictCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectBySubdistrictId(companyId);
        } else if (systemCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectCorrelationAll();
        }
        return subcontractors;
    }

    @Override
    public List<Subcontractor> getCorrelation(Serializable companyType, Serializable companyId,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return getBySystemUserRole((Integer)companyType, companyId, systemCompanyType, communityCompanyType,
            subdistrictCompanyType);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        Long communityResidentCount = communityResidentMapper.countBySubcontractorId(id);
        if (communityResidentCount != null && communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区分包人！");
        }
        Long dormitoryManagerCount = dormitoryManagerMapper.countBySubcontractorId(id);
        if (dormitoryManagerCount != null && dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区分包人！");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public List<Subcontractor> getByCommunityId(Serializable communityId) {
        return baseMapper.select(communityId);
    }

    @Override
    public Subcontractor getCorrelation(Serializable id) {
        return baseMapper.getCorrelationById(id);
    }

    /**
     * 通过系统用户角色查找社区分包人
     *
     * @param userLevel
     *            系统用户单位类型
     * @param companyId
     *            系统用户单位编号
     * @param systemCompanyType
     *            系统单位类型编号
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @return 对应的社区分包人对象集合
     */
    private List<Subcontractor> getBySystemUserRole(Integer userLevel, Serializable companyId,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        List<Subcontractor> subcontractors = null;
        if (communityCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.select(companyId);
        } else if (subdistrictCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectBySubdistrictId(companyId);
        } else if (systemCompanyType.equals(userLevel)) {
            subcontractors = baseMapper.selectCorrelationAll();
        }
        return subcontractors;
    }
}
