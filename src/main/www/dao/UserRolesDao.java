package www.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import www.entity.UserRole;

/**
 * 用户角色DAO接口
 *
 * @author 廿二月的天
 */
public interface UserRolesDao extends BaseDao<UserRole> {
    /**
     * 查询所有角色与所属系统用户
     *
     * @return 所有角色与所属系统用户
     * @throws DataAccessException 数据库操作异常
     */
    List<UserRole> selectRolesAndSystemUsersAll() throws DataAccessException;

    /**
     * 查询所有角色与所属权限
     *
     * @return 所有角色与所属权限
     * @throws DataAccessException 数据库操作异常
     */
    List<UserRole> selectRolesAndPrivilegesAll() throws DataAccessException;

    /**
     * 通过角色ID查询所有角色与所属权限
     *
     * @param roleId 系统角色编号
     * @return 所有角色与所属权限
     * @throws DataAccessException 数据库操作异常
     */
    UserRole selectRolesAndPrivilegesById(Integer roleId) throws DataAccessException;

    /**
     * 通过角色ID查询角色名称
     *
     * @param roleId 系统角色编号
     * @return 系统角色名称
     * @throws DataAccessException 数据库操作异常
     */
    String selectRoleNameById(Integer roleId) throws DataAccessException;
}
