package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.RolePrivilegeRelation;

/**
 * 系统用户角色权限中间业务接口
 *
 * @author 廿二月的天
 */
public interface UserRolePrivilegeService extends BaseService<RolePrivilegeRelation> {
    /**
     * 通过中间表对象删除
     *
     * @param rolePrivilegeRelation
     *            系统用户角色权限中间对象
     * @return 数据库删除影响的行数
     */
    boolean remove(RolePrivilegeRelation rolePrivilegeRelation);
}
