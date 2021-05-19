package com.github.phonenumbermanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityMapper;
import com.github.phonenumbermanager.service.CommunityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 社区业务实现
 *
 * @author 廿二月的天
 */
@Service("communityService")
public class CommunityServiceImpl extends BaseServiceImpl<CommunityMapper, Community> implements CommunityService {

    @Override
    public Community getCorrelation(Serializable communityId) {
        return communityMapper.selectAndSubdistrictById(communityId);
    }

    @Override
    public IPage<Community> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Page<Community> page = new Page<>(pageNumber, pageDataSize);
        return communityMapper.selectAndSubdistrictAll(page);
    }

    @Override
    public List<Community> getCorrelation() {
        return communityMapper.selectAndSubdistrictAll();
    }

    @Override
    public List<Community> get(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        List<Community> communities;
        if (communityCompanyType.equals(systemUser.getLevel().getValue())) {
            communities = new ArrayList<>();
            Community community = communityMapper.selectCorrelationById(systemUser.getCompanyId());
            communities.add(community);
        } else if (subdistrictCompanyType.equals(systemUser.getLevel().getValue())) {
            communities = communityMapper.selectCorrelationBySubdistrictId(systemUser.getCompanyId());
        } else {
            communities = communityMapper.selectCorrelationSubdistrictsAll();
        }
        return communities;
    }

    @Override
    public List<Community> getBySubdistrictId(Serializable subdistrictId) {
        return communityMapper.selectBySubdistrictId(subdistrictId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(List<Map<String, Object>> data, Integer changeType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
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
        return super.update(community, wrapper);
    }

    @Override
    public boolean isSubmittedById(Integer submitType, Serializable id) {
        return communityMapper.selectSubmittedById(submitType, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        Long communityResidentCount = communityResidentMapper.countByCommunityId(id);
        if (communityResidentCount != null && communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        Long dormitoryManagerCount = dormitoryManagerMapper.countByCommunityId(id);
        if (dormitoryManagerCount != null && dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        communityMapper.deleteById(id);
        return true;
    }
}
