package la.isx.phone_number_manager.main.dao;

import java.util.List;

import la.isx.phone_number_manager.main.entity.UserRole;
/**
 * 用户角色DAO接口
 *
 */
public interface UserRolesDao extends BaseDao<UserRole> {
	/**
	 * 查询所有角色与所属系统用户
	 * @return
	 * @throws Exception
	 */
	public List<UserRole> selectRolesAndSystemUsersAll() throws Exception;
	/**
	 * 查询所有角色与所属权限
	 * @return
	 * @throws Exception
	 */
	public List<UserRole> selectRolesAndPrivilegesAll() throws Exception;
	/**
	 * 通过角色ID查询所有角色与所属权限
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public UserRole selectRolesAndPrivilegesById(Integer roleId) throws Exception;
}