package com.github.phonenumbermanager.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.SystemUser;

/**
 * 社区业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityService extends BaseService<Community> {

    /**
     * 通过系统用户查找
     *
     * @param systemUser
     *            系统用户对象
     * @param communityCompanyType
     *            社区级角色编号
     * @param subdistrictCompanyType
     *            街道级角色编号
     * @return 查找到的社区对象的集合
     */
    List<Community> get(SystemUser systemUser, Serializable communityCompanyType, Serializable subdistrictCompanyType);

    /**
     * 通过街道编号查找
     *
     * @param subdistrictId
     *            街道编号
     * @return 查找到的社区对象的集合
     */
    List<Community> getBySubdistrictId(Serializable subdistrictId);

    /**
     * 更改是否允许更删改信息
     *
     * @param data
     *            用户数据
     * @param changeType
     *            更改类型
     * @param communityCompanyType
     *            社区级角色编号
     * @param subdistrictCompanyType
     *            街道级角色编号
     * @return 是否更改成功
     */
    boolean update(List<Map<String, Object>> data, Integer changeType, Serializable communityCompanyType,
        Serializable subdistrictCompanyType);

    /**
     * 是否已经提报
     *
     * @param submitType
     *            提交类型
     * @param id
     *            查询编号
     * @return 是否已经提报
     */
    boolean isSubmittedById(Integer submitType, Serializable id);
}
