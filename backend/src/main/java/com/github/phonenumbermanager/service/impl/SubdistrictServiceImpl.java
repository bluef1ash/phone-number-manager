package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.UserLevelEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
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

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public IPage<Subdistrict> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Page<Subdistrict> page = new Page<>(pageNumber, pageDataSize);
        return subdistrictMapper.selectAndPhoneNumbersAll(page);
    }

    @Override
    public Set<Subdistrict> getCorrelation(Serializable systemCompanyType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType, UserLevelEnum userLevel, Serializable companyId) {
        Set<Subdistrict> subdistricts;
        if (communityCompanyType.equals(userLevel.getValue())) {
            // 社区角色
            subdistricts = new HashSet<>();
            Subdistrict subdistrict = subdistrictMapper.selectOneAndCommunityByCommunityId(companyId);
            subdistricts.add(subdistrict);
        } else if (subdistrictCompanyType.equals(userLevel.getValue())) {
            // 街道角色
            subdistricts = subdistrictMapper.selectCorrelationAndCommunityById(companyId);
        } else {
            // 管理员角色
            subdistricts = subdistrictMapper.selectAndCommunities();
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
        return subdistrictMapper.deleteById(id) > 0;
    }

    @Override
    public Subdistrict getCorrelation(Serializable id) {
        return subdistrictMapper.selectCorrelationAndPhoneNumbersById(id);
    }
}
