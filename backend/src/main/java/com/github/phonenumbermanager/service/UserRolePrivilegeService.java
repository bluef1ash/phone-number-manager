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
     * @param roleId
     *            所属角色编号
     * @param privilegeId
     *            所属用户权限编号
     * @return 数据库删除影响的行数
     */
    boolean remove(Long roleId, Long privilegeId);
}
