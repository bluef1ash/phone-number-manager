package www.service.impl;

import org.springframework.stereotype.Service;
import www.entity.UserPrivilege;
import www.service.UserPrivilegeService;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userPrivilegeService")
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilege> implements UserPrivilegeService {
    @Override
    public Set<UserPrivilege> findPrivilegesByRoleId(Long roleId) throws Exception {
        return userPrivilegesDao.selectPrivilegesByRoleId(roleId);
    }

    @Override
    public Set<UserPrivilege> findPrivilegesByIsDisplay(Integer isDisplay, Set<UserPrivilege> userPrivileges) throws Exception {
        //userPrivileges;
        return findPrivilegesAndSubPrivileges(userPrivileges, isDisplay, 0L);
    }

    @Override
    public Set<UserPrivilege> findPrivilegesAndSubPrivilegesAll() throws Exception {
        Set<UserPrivilege> userPrivileges = new HashSet<>(userPrivilegesDao.selectObjectsAll());
        // userPrivileges;
        return findPrivilegesAndSubPrivileges(userPrivileges, null, 0L);
    }

    /**
     * 有序排列用户权限
     *
     * @param userPrivileges 需要排列的系统用户权限对象集合
     * @param isDisplay      是否在导航栏中显示
     * @return 排列成功的统用户权限对象集合
     */
    private Set<UserPrivilege> findPrivilegesAndSubPrivileges(Set<UserPrivilege> userPrivileges, Integer isDisplay, Long rootId) {
        Set<UserPrivilege> newUserPrivileges = new LinkedHashSet<>();
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (rootId.equals(userPrivilege.getHigherPrivilege())) {
                if (isDisplay != null && !isDisplay.equals(userPrivilege.getIsDisplay())) {
                    continue;
                }
                UserPrivilege newUserPrivilege = new UserPrivilege();
                newUserPrivilege.setPrivilegeId(userPrivilege.getPrivilegeId());
                newUserPrivilege.setPrivilegeName(userPrivilege.getPrivilegeName());
                newUserPrivilege.setConstraintAuth(userPrivilege.getConstraintAuth());
                newUserPrivilege.setHigherPrivilege(userPrivilege.getHigherPrivilege());
                newUserPrivilege.setIconName(userPrivilege.getIconName());
                newUserPrivilege.setIsDisplay(userPrivilege.getIsDisplay());
                newUserPrivilege.setOrders(userPrivilege.getOrders());
                newUserPrivilege.setUri(userPrivilege.getUri());
                newUserPrivilege.setSubUserPrivileges(findPrivilegesAndSubPrivileges(userPrivileges, isDisplay, userPrivilege.getPrivilegeId()));
                newUserPrivileges.add(newUserPrivilege);
            }
        }
        return newUserPrivileges;
    }
}
