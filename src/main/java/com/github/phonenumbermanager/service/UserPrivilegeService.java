package com.github.phonenumbermanager.service;


import com.github.phonenumbermanager.entity.UserPrivilege;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;

/**
 * 系统用户权限业务接口
 *
 * @author 廿二月的天
 */
public interface UserPrivilegeService extends BaseService<UserPrivilege> {
    /**
     * 通过角色ID查找
     *
     * @param roleId 系统用户角色编号
     * @return 系统用户权限对象的集合
     * @throws Exception SERVICE层异常
     */
    Set<UserPrivilege> findByRoleId(Serializable roleId) throws Exception;

    /**
     * 通过权限是否在导航栏显示查找
     *
     * @param display        是否在导航栏中显示
     * @param userPrivileges 权限集合
     * @return 查找到的系统用户权限对象的集合
     * @throws Exception SERVICE层异常
     */
    Set<UserPrivilege> find(Boolean display, Set<UserPrivilege> userPrivileges) throws Exception;

    /**
     * 查找所有权限包含子权限
     *
     * @return 所有系统用户权限与对应子权限的集合
     * @throws Exception SERVICE层异常
     */
    Set<UserPrivilege> findForSub() throws Exception;

    /**
     * 查找所有权限并处理
     *
     * @return 处理后的系统用户权限
     * @throws Exception SERVICE层异常
     */
    LinkedList<UserPrivilege> findAndHandler() throws Exception;
}
