package main.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import main.entity.UserRole;
import main.entity.UserRolePrivilege;
import main.service.UserRolePrivilegeService;

/**
 * 系统用户权限业务实现
 *
 */
@Service("userRoleSrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<UserRolePrivilege> implements UserRolePrivilegeService {
	@Override
	public int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege) {
		return userRolePrivilegesDao.deleteUserRolePrivilegeByUserRolePrivilege(userRolePrivilege);
	}
	@Override
	public int addUserRolePrivileges(UserRole userRole, Integer[] privilegeIds) {
		List<UserRolePrivilege> userRolePrivileges = new ArrayList<UserRolePrivilege>();
		UserRolePrivilege userRolePrivilege = null;
		for (Integer privilegeId : privilegeIds) {
			if (privilegeId != 0) {
				userRolePrivilege = new UserRolePrivilege();
				userRolePrivilege.setRoleId(userRole.getRoleId());
				userRolePrivilege.setPrivilegeId(privilegeId);
				userRolePrivileges.add(userRolePrivilege);
			}
		}
		return userRolePrivilegesDao.insertUserRolePrivileges(userRolePrivileges);
	}
}