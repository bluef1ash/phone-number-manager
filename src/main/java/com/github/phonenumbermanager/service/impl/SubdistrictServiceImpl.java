package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 街道业务实现
 *
 * @author 廿二月的天
 */
@Service("subdistrictService")
public class SubdistrictServiceImpl extends BaseServiceImpl<Subdistrict> implements SubdistrictService {

    @Override
    public long create(Subdistrict subdistrict) {
        subdistrict.setCreateTime(DateUtils.getTimestamp(new Date()));
        subdistrict.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(subdistrict);
    }

    @Override
    public long update(Subdistrict subdistrict) {
        subdistrict.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(subdistrict);
    }

    @Override
    public Set<Subdistrict> findCorrelation(Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, Serializable companyType, Serializable companyId) {
        Set<Subdistrict> subdistricts;
        if (companyType.equals(communityCompanyType)) {
            // 社区角色
            subdistricts = new HashSet<>();
            Subdistrict subdistrict = subdistrictDao.selectOneAndCommunityByCommunityId(companyId);
            subdistricts.add(subdistrict);
        } else if (companyType.equals(subdistrictCompanyType)) {
            // 街道角色
            subdistricts = subdistrictDao.selectCorrelationAndCommunityById(companyId);
        } else {
            // 管理员角色
            subdistricts = subdistrictDao.selectAndCommunities();
        }
        return subdistricts;
    }

    @Override
    public long delete(Serializable id) {
        List<Community> communities = communityDao.selectBySubdistrictId(id);
        if (communities == null || communities.size() == 0) {
            throw new BusinessException("不允许删除存在下属社区的街道单位！");
        }
        return super.delete(id);
    }
}
