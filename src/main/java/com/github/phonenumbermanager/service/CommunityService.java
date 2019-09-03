package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.SystemUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 社区业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityService extends BaseService<Community> {
    /**
     * 通过社区ID查找社区和所属街道
     *
     * @param communityId 社区编号
     * @return 查找到的社区和所属街道
     * @throws Exception SERVICE层异常
     */
    Community findCorrelation(Serializable communityId) throws Exception;

    /**
     * 查找所有社区和所属街道
     *
     * @return 查找到的所有社区和所属街道
     * @throws Exception SERVICE层异常
     */
    List<Community> findCorrelation() throws Exception;

    /**
     * 通过系统用户查找
     *
     * @param systemUser             系统用户对象
     * @param communityCompanyType   社区级角色编号
     * @param subdistrictCompanyType 街道级角色编号
     * @return 查找到的社区对象的集合
     * @throws Exception SERVICE层异常
     */
    List<Community> find(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType) throws Exception;

    /**
     * 通过街道编号查找
     *
     * @param subdistrictId 街道编号
     * @return 查找到的社区对象的集合
     * @throws Exception SERVICE层异常
     */
    List<Community> findBySubdistrictId(Serializable subdistrictId) throws Exception;

    /**
     * 更改是否允许更删改信息
     *
     * @param data                   用户数据
     * @param changeType             更改类型
     * @param systemCompanyType      系统级角色编号
     * @param communityCompanyType   社区级角色编号
     * @param subdistrictCompanyType 街道级角色编号
     * @return 是否更改成功
     * @throws Exception SERVICE层异常
     */
    long update(List<Map<String, Object>> data, Integer changeType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) throws Exception;
}
