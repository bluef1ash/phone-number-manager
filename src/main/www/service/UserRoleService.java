package www.service;

import java.util.Map;

import www.entity.UserRole;

/**
 * 系统用户角色业务接口
 */
public interface UserRoleService extends BaseService<UserRole> {
    /**
     * 查找所有角色与所属系统用户（包含分页）
     *
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> findRolesAndSystemUsersAll(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 查找所有角色与所属权限（包含分页）
     *
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> findRolesAndPrivileges(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过角色ID查找角色与所属权限（包含分页）
     *
     * @param roleId
     * @return
     * @throws Exception
     */
    public UserRole findRolesAndPrivileges(Integer roleId) throws Exception;
}
