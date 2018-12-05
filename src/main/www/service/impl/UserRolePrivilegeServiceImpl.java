package www.service.impl;

import org.springframework.stereotype.Service;
import www.entity.UserRole;
import www.entity.UserRolePrivilege;
import www.service.UserRolePrivilegeService;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userRolePrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<UserRolePrivilege> implements UserRolePrivilegeService {
    @Override
    public int deleteUserRolePrivilegeByUserRolePrivilege(UserRolePrivilege userRolePrivilege) {
        return userRolePrivilegesDao.deleteUserRolePrivilegeByUserRolePrivilege(userRolePrivilege);
    }

    @Override
    public int addUserRolePrivileges(UserRole userRole, Long[] privilegeIds) {
        List<UserRolePrivilege> userRolePrivileges = new ArrayList<>();
        UserRolePrivilege userRolePrivilege;
        for (Long privilegeId : privilegeIds) {
            if (privilegeId != 0) {
                userRolePrivilege = new UserRolePrivilege();
                userRolePrivilege.setRoleId(userRole.getRoleId());
                userRolePrivilege.setPrivilegeId(privilegeId);
                userRolePrivileges.add(userRolePrivilege);
            }
        }
        return userRolePrivilegesDao.insertUserRolePrivileges(userRolePrivileges);
    }
}
