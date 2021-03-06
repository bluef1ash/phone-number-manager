package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.Subdistrict;

import java.io.Serializable;
import java.util.Set;

/**
 * 街道业务接口
 *
 * @author 廿二月的天
 */
public interface SubdistrictService extends BaseService<Subdistrict> {
    /**
     * 通过角色查找街道及社区居委会
     *
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param companyType            系统用户单位类型编号
     * @param companyId              系统用户单位编号
     * @return 查找到的街道及社区居委会的集合
     * @throws Exception SERVICE层异常
     */
    Set<Subdistrict> findCorrelation(Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, Serializable companyType, Serializable companyId) throws Exception;
}
