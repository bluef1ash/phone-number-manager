package main.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import main.entity.UserRole;
import main.service.UserRoleService;

/**
 * 系统用户角色业务实现
 *
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements UserRoleService {
	@Override
	public Map<String, Object> findRolesAndSystemUsersAll(Integer pageNum, Integer pageSize) throws Exception {
		List<UserRole> data = userRolesDao.selectRolesAndSystemUsersAll();
		return findObjectsMethod(data, pageNum, pageSize);
	}
	@Override
	public Map<String, Object> findRolesAndPrivileges(Integer pageNum, Integer pageSize) throws Exception {
		List<UserRole> data = userRolesDao.selectRolesAndPrivilegesAll();
		return findObjectsMethod(data, pageNum, pageSize);
	}
	@Override
	public UserRole findRolesAndPrivileges(Integer roleId) throws Exception {
		return userRolesDao.selectRolesAndPrivilegesById(roleId);
	}
}