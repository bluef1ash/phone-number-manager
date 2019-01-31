package www.service;

import www.entity.UserRole;

import java.util.List;
import java.util.Map;

/**
 * 系统用户角色业务接口
 *
 * @author 廿二月的天
 */
public interface UserRoleService extends BaseService<UserRole> {
    /**
     * 查找所有角色与所属系统用户（包含分页）
     *
     * @param pageNumber  分页页码
     * @param pageDataSize 每页显示的条目数
     * @return 查找到的所有角色与所属系统用户与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findRolesAndSystemUsersAll(Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 查找所有角色与所属权限（包含分页）
     *
     * @param pageNumber  分页页码
     * @param pageDataSize 每页显示的条目数
     * @return 查找到的所有角色与所属系统权限与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findRolesAndPrivileges(Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过角色ID查找角色与所属权限
     *
     * @param roleId 系统用户角色编号
     * @return 查找到的系统用户角色对象
     * @throws Exception SERVICE层异常
     */
    UserRole findRolesAndPrivileges(Long roleId) throws Exception;

    /**
     * 通过角色名称查找角色
     *
     * @param roleName 系统用户角色名称
     * @return 查找到的系统用户角色对象的集合
     * @throws Exception SERVICE层异常
     */
    List<UserRole> findRolesByRoleName(String roleName) throws Exception;
}
