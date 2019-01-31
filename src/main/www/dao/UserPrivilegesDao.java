package www.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import www.entity.UserPrivilege;

import java.util.Set;

/**
 * 用户权限DAO接口
 *
 * @author 廿二月的天
 */
public interface UserPrivilegesDao extends BaseDao<UserPrivilege> {
    /**
     * 通过角色ID查询
     *
     * @param roleId 系统用户角色编号
     * @return 系统用户权限列表
     * @throws DataAccessException 数据库操作异常
     */
    Set<UserPrivilege> selectPrivilegesByRoleId(Long roleId) throws DataAccessException;

    /**
     * 通过角色是否在导航栏显示查询
     *
     * @param isDisplay 是否在导航栏中显示
     * @return 系统用户权限列表
     * @throws DataAccessException 数据库操作异常
     */
    Set<UserPrivilege> selectPrivilegesByIsDisplay(Integer isDisplay) throws DataAccessException;

    /**
     * 通过角色是否在导航栏和拥有的权限显示查询
     *
     * @param isDisplay        是否在菜单中显示
     * @param constraintAuthes 系统应用集合
     * @return 系统用户权限列表
     * @throws DataAccessException 数据库操作异常
     */
    Set<UserPrivilege> selectPrivilegesByIsDisplayAndConstraintAuth(@Param("isDisplay") Integer isDisplay, @Param("constraintAuthes") Set<String> constraintAuthes) throws DataAccessException;

    /**
     * 通过角色父级ID查询
     *
     * @param higherPrivilege 父级权限编号
     * @param isDisplay       是否在导航栏中显示
     * @return 系统用户权限集合
     * @throws DataAccessException 数据库操作异常
     */
    Set<UserPrivilege> selectPrivilegesByHigherPrivilegeAndIsDisplay(@Param("higherPrivilege") Long higherPrivilege, @Param("isDisplay") Integer isDisplay) throws DataAccessException;

    /**
     * 通过角色父级ID查询
     *
     * @param higherPrivilege 父级权限编号
     * @return 系统用户权限集合
     * @throws DataAccessException 数据库操作异常
     */
    Set<UserPrivilege> selectPrivilegesByHigherPrivilege(Long higherPrivilege) throws DataAccessException;
}
