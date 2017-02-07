package la.isx.phone_number_manager.main.service;

import la.isx.phone_number_manager.main.entity.UserRole;
import la.isx.phone_number_manager.main.entity.UserRolePrivilege;
/**
 * 系统用户角色权限中间业务接口
 * 
 */
public interface UserRolePrivilegeService extends BaseService<UserRolePrivilege> {
	/**
	 * 批量添加
	 * @param userRolePrivileges
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