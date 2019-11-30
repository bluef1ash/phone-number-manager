package com.github.phonenumbermanager.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 社区业务实现
 *
 * @author 廿二月的天
 */
@Service("communityService")
public class CommunityServiceImpl extends BaseServiceImpl<Community> implements CommunityService {


    @Override
    public long create(Community community) {
        community.setCreateTime(DateUtils.getTimestamp(new Date()));
        community.setUpdateTime(DateUtils.getTimestamp(new Date()));
        community.setDormitorySubmitted(false);
        community.setResidentSubmitted(false);
        return super.create(community);
    }

    @Override
    public long update(Community community) {
        community.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(community);
    }

    @Override
    public Community findCorrelation(Serializable communityId) {
        return communityDao.selectAndSubdistrictById(communityId);
    }

    @Override
    public Map<String, Object> findCorrelation(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        PageHelper.startPage(pageNumber, pageDataSize);
        List<Community> data = communityDao.selectAndSubdistrictAll();
        return find(data);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public List<Community> findCorrelation() {
        return communityDao.selectAndSubdistrictAll();
    }

    @Override
    public List<Community> find(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        List<Community> communities;
        if (communityCompanyType.equals(systemUser.getCompanyType())) {
            communities = new ArrayList<>();
            Community community = communityDao.selectCorrelationById(systemUser.getCompanyId());
            communities.add(community);
        } else if (subdistrictCompanyType.equals(systemUser.getCompanyType())) {
            communities = communityDao.selectCorrelationBySubdistrictId(systemUser.getCompanyId());
        } else {
            communities = communityDao.selectCorrelationSubdistrictsAll();
        }
        return communities;
    }

    @Override
    public List<Community> findBySubdistrictId(Serializable subdistrictId) {
        return communityDao.selectBySubdistrictId(subdistrictId);
    }

    @Override
    public long update(List<Map<String, Object>> data, Integer changeType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return communityDao.updateSubmitted(data, changeType, systemCompanyType, communityCompanyType, subdistrictCompanyType);
    }

    @Override
    public long delete(Serializable id) {
        Long communityResidentCount = communityResidentDao.countByCommunityId(id);
        if (communityResidentCount != null && communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        Long dormitoryManagerCount = dormitoryManagerDao.countByCommunityId(id);
        if (dormitoryManagerCount != null && dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        return super.delete(id);
    }
}
