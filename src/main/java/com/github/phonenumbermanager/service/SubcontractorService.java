package com.github.phonenumbermanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.UserLevelEnum;
import com.github.phonenumbermanager.entity.Subcontractor;

import java.io.Serializable;
import java.util.List;

/**
 * 社区分包人Service接口
 *
 * @author 廿二月的天
 */
public interface SubcontractorService extends BaseService<Subcontractor> {
    /**
     * 通过系统用户角色编号与系统用户角色定位编号分页查找
     *
     * @param pageNumber             分页页码
     * @param pageDataSize           每页展示数据数量
     * @param userLevel              系统用户单位类型
     * @param companyId              系统用户单位编号
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 社区分包人对象集合与分页对象
     */
    IPage<Subcontractor> get(Integer pageNumber, Integer pageDataSize, UserLevelEnum userLevel, Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType);

    /**
     * 通过系统用户角色查找
     *
     * @param companyType            系统用户单位类型
     * @param companyId              系统用户单位编号
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 社区分包人对象集合
     */
    List<Subcontractor> getCorrelation(Serializable companyType, Serializable companyId, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType);

    /**
     * 通过社区编号查找
     *
     * @param communityId 社区编号
     * @return 社区分包人对象集合
     */
    List<Subcontractor> getByCommunityId(Serializable communityId);
}
