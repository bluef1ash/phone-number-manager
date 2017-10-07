package www.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import www.entity.UserPrivilege;

/**
 * 系统用户权限业务接口
 */
public interface UserPrivilegeService extends BaseService<UserPrivilege> {
    /**
     * 通过角色ID查找
     *
     * @param roleId
     * @return
     * @throws Exception
     */
    public List<UserPrivilege> findPrivilegesByRoleId(Integer roleId) throws Exception;

    /**
     * 通过权限是否在导航栏显示查找
     *
     * @param isDisplay
     * @return
     * @throws Exception
     */
    public List<UserPrivilege> findPrivilegesByIsDisplay(Integer isDisplay, HttpSession session) throws Exception;

    /**
     * 查找所有权限包含子权限
     *
     * @return
     * @throws Exception
     */
    public List<UserPrivilege> findPrivilegesAndsubPrivilegesAll() throws Exception;
}
