package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 社区分包人Service实现
 *
 * @author 廿二月的天
 */
@Service("subcontractorService")
public class SubcontractorServiceImpl extends BaseServiceImpl<Subcontractor> implements SubcontractorService {

    @Override
    public long create(Subcontractor subcontractor) {
        subcontractor.setCreateTime(DateUtils.getTimestamp(new Date()));
        subcontractor.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(subcontractor);
    }

    @Override
    public long update(Subcontractor subcontractor) {
        subcontractor.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(subcontractor);
    }

    @Override
    public Map<String, Object> find(Integer pageNumber, Integer pageDataSize, Serializable companyType, Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        setPageHelper(pageNumber, pageDataSize);
        List<Subcontractor> subcontractors = findBySystemUserRole(companyType, companyId, systemCompanyType, communityCompanyType, subdistrictCompanyType);
        return find(subcontractors);
    }

    @Override
    public List<Subcontractor> findCorrelation(Serializable companyType, Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return findBySystemUserRole(companyType, companyId, systemCompanyType, communityCompanyType, subdistrictCompanyType);
    }

    @Override
    public long delete(Serializable id) {
        Long communityResidentCount = communityResidentDao.countBySubcontractorId(id);
        if (communityResidentCount == null || communityResidentCount == 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区分包人！");
        }
        Long dormitoryManagerCount = dormitoryManagerDao.countBySubcontractorId(id);
        if (dormitoryManagerCount == null || dormitoryManagerCount == 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区分包人！");
        }
        return super.delete(id);
    }

    @Override
    public List<Subcontractor> findByCommunityId(Serializable communityId) {
        return subcontractorDao.select(communityId);
    }

    /**
     * 通过系统用户角色查找社区分包人
     *
     * @param companyType            系统用户单位类型
     * @param companyId              系统用户单位编号
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 对应的社区分包人对象集合
     */
    private List<Subcontractor> findBySystemUserRole(Serializable companyType, Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        List<Subcontractor> subcontractors = null;
        if (companyType.equals(communityCompanyType)) {
            subcontractors = subcontractorDao.select(companyId);
        } else if (companyType.equals(subdistrictCompanyType)) {
            subcontractors = subcontractorDao.selectBySubdistrictId(companyId);
        } else if (companyType.equals(systemCompanyType)) {
            subcontractors = subcontractorDao.selectCorrelationAll();
        }
        return subcontractors;
    }
}
