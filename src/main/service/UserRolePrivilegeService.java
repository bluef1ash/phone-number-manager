package main.service;

import main.entity.UserRole;
import main.entity.UserRolePrivilege;
/**
 * 系统用户角色权限中间业务接口
 * 
 */
public interface UserRolePrivilegeService extends BaseService<UserRolePrivilege> {
	/**
	 * 批量添加
	 * @param userRole
	 * @param privilegeIds
	 * @return
	 */
	public int addUserRolePrivileges(UserRole userRole, Integer[] privilegeIds);
	/**
	 * 通过中间表对象删除
	 * @param userRolePrivilege
	 * @return
	 */
	public int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege);
}