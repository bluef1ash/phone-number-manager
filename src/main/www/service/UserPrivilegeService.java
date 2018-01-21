package www.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import www.entity.UserPrivilege;

/**
 * 系统用户权限业务接口
 *
 * @author 廿二月的天
 */
public interface UserPrivilegeService extends BaseService<UserPrivilege> {
    /**
     * 通过角色ID查找
     *
     * @param roleId 系统用户角色编号
     * @return 系统用户权限对象的集合
     * @throws Exception SERVICE层异常
     */
    List<UserPrivilege> findPrivilegesByRoleId(Integer roleId) throws Exception;

    /**
     * 通过权限是否在导航栏显示查找
     *
     * @param isDisplay 是否在导航栏中显示
     * @param session   session对象
     * @return 查找到的系统用户权限对象的集合
     * @throws Exception SERVICE层异常
     */
    List<UserPrivilege> findPrivilegesByIsDisplay(Integer isDisplay, HttpSession session) throws Exception;

    /**
     * 查找所有权限包含子权限
     *
     * @return 所有系统用户权限与对应子权限的集合
     * @throws Exception SERVICE层异常
     */
    List<UserPrivilege> findPrivilegesAndSubPrivilegesAll() throws Exception;
}
