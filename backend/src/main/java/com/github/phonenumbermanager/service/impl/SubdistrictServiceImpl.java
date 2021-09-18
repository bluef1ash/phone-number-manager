package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.enums.UserLevelEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CommunityMapper;
import com.github.phonenumbermanager.mapper.SubdistrictMapper;
import com.github.phonenumbermanager.service.SubdistrictService;

/**
 * 街道业务实现
 *
 * @author 廿二月的天
 */
@Service("subdistrictService")
public class SubdistrictServiceImpl extends BaseServiceImpl<SubdistrictMapper, Subdistrict>
    implements SubdistrictService {
    @Resource
    private CommunityMapper communityMapper;

    @Override
    public IPage<Subdistrict> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Page<Subdistrict> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectAndPhoneNumbersAll(page);
    }

    @Override
    public Set<Subdistrict> getCorrelation(Serializable systemCompanyType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType, UserLevelEnum userLevel, Serializable companyId) {
        Set<Subdistrict> subdistricts;
        if (communityCompanyType.equals(userLevel.getValue())) {
            // 社区角色
            subdistricts = new HashSet<>();
            Subdistrict subdistrict = baseMapper.selectOneAndCommunityByCommunityId(companyId);
            subdistricts.add(subdistrict);
        } else if (subdistrictCompanyType.equals(userLevel.getValue())) {
            // 街道角色
            subdistricts = baseMapper.selectCorrelationAndCommunityById(companyId);
        } else {
            // 管理员角色
            subdistricts = baseMapper.selectAndCommunities();
        }
        return subdistricts;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        List<Community> communities = communityMapper.selectBySubdistrictId(id);
        if (communities != null && communities.size() > 0) {
            throw new BusinessException("不允许删除存在下属社区的街道单位！");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public Subdistrict getCorrelation(Serializable id) {
        return baseMapper.selectCorrelationAndPhoneNumbersById(id);
    }
}
