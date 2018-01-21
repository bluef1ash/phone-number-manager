package www.service;

import www.entity.UserRole;
import www.entity.UserRolePrivilege;

/**
 * 系统用户角色权限中间业务接口
 *
 * @author 廿二月的天
 */
public interface UserRolePrivilegeService extends BaseService<UserRolePrivilege> {
    /**
     * 批量添加
     *
     * @param userRole     系统用户角色对象
     * @param privilegeIds 系统用户权限编号数组
     * @return 数据库添加影响的行数
     * @throws Exception SERVICE层异常
     */
    int addUserRolePrivileges(UserRole userRole, Integer[] privilegeIds) throws Exception;

    /**
     * 通过中间表对象删除
     *
     * @param userRolePrivilege 系统用户角色权限中间对象
     * @return 数据库删除影响的行数
     * @throws Exception SERVICE层异常
     */
    int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege) throws Exception;
}
