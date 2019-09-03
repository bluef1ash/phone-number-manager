package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.SystemUser;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser> {

    /**
     * 通过系统用户编号查找系统用户和关联角色
     *
     * @param id 系统用户编号
     * @return 查找到的系统用户对象
     * @throws Exception SERVICE层异常
     */
    SystemUser findCorrelation(Serializable id) throws Exception;

    /**
     * 查找系统用户编号与名称
     *
     * @return 查找到的系统用户对象集合
     * @throws Exception SERVICE层异常
     */
    List<SystemUser> findIdAndName() throws Exception;
}
