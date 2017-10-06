package main.dao;

import java.util.List;

import main.entity.UserRolePrivilege;

/**
 * 系统用户角色与权限中间DAO接口
 *
 */
public interface UserRolePrivilegesDao extends BaseDao<UserRolePrivilege> {
	/**
	 * 批量添加
	 * @param userRolePrivileges
	 * @return
	 */
	public int insertUserRolePrivileges(List<UserRolePrivilege> userRolePrivileges);
	/**
	 * 通过中间表对象删除
	 * @param userRolePrivilege
	 * @return
	 */
	public int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege);
}