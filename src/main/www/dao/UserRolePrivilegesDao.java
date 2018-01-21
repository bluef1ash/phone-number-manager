package www.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import www.entity.UserRolePrivilege;

/**
 * 系统用户角色与权限中间DAO接口
 *
 * @author 廿二月的天
 */
public interface UserRolePrivilegesDao extends BaseDao<UserRolePrivilege> {
    /**
     * 批量添加
     *
     * @param userRolePrivileges 系统用户权限集合
     * @return 插入记录数
     * @throws DataAccessException 数据库操作异常
     */
    int insertUserRolePrivileges(List<UserRolePrivilege> userRolePrivileges) throws DataAccessException;

    /**
     * 通过中间表对象删除
     *
     * @param userRolePrivilege 系统用户
     * @return 删除成功记录数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege) throws DataAccessException;
}
