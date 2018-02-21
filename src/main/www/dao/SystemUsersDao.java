package www.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import www.entity.SystemUser;

/**
 * 系统用户DAO接口
 *
 * @author 廿二月的天
 */
public interface SystemUsersDao extends BaseDao<SystemUser> {
    /**
     * 查询所有系统用户与所属角色
     *
     * @return 所有系统用户与所属角色
     * @throws DataAccessException 数据库操作异常
     */
    List<SystemUser> selectSystemUsersAndRolesAll() throws DataAccessException;

    /**
     * 通过用户ID查询系统用户与所属角色
     *
     * @param id 用户编号
     * @return 查询到的系统用户与所属角色
     * @throws DataAccessException 数据库操作异常
     */
    SystemUser selectSystemUserAndRoleById(Integer id) throws DataAccessException;

    /**
     * 通过用户名称查询系统用户与所属角色以及权限
     *
     * @param username 系统用户名称
     * @return 查询到的系统用户与所属角色以及权限
     * @throws DataAccessException 数据库操作异常
     */
    SystemUser selectSystemUserAndRoleAndPrivilegesByName(String username) throws DataAccessException;

    /**
     * 查询系统用户（不含密码）
     *
     * @return 查询到的系统用户
     * @throws DataAccessException 数据库操作异常
     */
    List<SystemUser> selectSystemUsersAll() throws DataAccessException;
}
