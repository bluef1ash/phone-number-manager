package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRolePrivilege;
import com.github.phonenumbermanager.mapper.UserPrivilegeMapper;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userPrivilegeService")
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilegeMapper, UserPrivilege> implements UserPrivilegeService {

    @Override
    public Set<UserPrivilege> getByRoleId(Serializable roleId) {
        return userPrivilegeMapper.selectByRoleId(roleId);
    }

    @Override
    public Set<UserPrivilege> get(Boolean display, Set<UserPrivilege> userPrivileges) {
        return getAndSubPrivileges(userPrivileges, display, 0L);
    }

    @Override
    public Set<UserPrivilege> getForSub() {
        Set<UserPrivilege> userPrivileges = new HashSet<>(userPrivilegeMapper.selectList(null));
        return getAndSubPrivileges(userPrivileges, null, 0L);
    }

    @Override
    public LinkedList<UserPrivilege> getAndHandler() {
        List<UserPrivilege> userPrivileges = userPrivilegeMapper.selectList(null);
        LinkedList<UserPrivilege> privileges = new LinkedList<>();
        getAndSubPrivileges(privileges, userPrivileges, 0L, 0);
        for (UserPrivilege userPrivilege : privileges) {
            StringBuilder name = new StringBuilder();
            name.append("|");
            for (int i = 0; i < userPrivilege.getLevel(); i++) {
                name.append("  ");
            }
            name.append("┗");
            userPrivilege.setName(name.toString() + userPrivilege.getName());
        }
        return privileges;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        if (systemUserMapper.selectByRoleId(id).size() > 0) {
            return false;
        }
        UserRolePrivilege userRolePrivilege = new UserRolePrivilege();
        userRolePrivilege.setPrivilegeId((Long) id);
        if (userRolePrivilegeMapper.deleteByUserRolePrivilege(userRolePrivilege) > 0) {
            return userPrivilegeMapper.deleteById(id) > 0;
        }
        return false;
    }

    /**
     * 有序排列用户权限
     *
     * @param userPrivileges 需要排列的系统用户权限对象集合
     * @param display        是否在导航栏中显示
     * @return 排列成功的统用户权限对象集合
     */
    private Set<UserPrivilege> getAndSubPrivileges(Set<UserPrivilege> userPrivileges, Boolean display, Serializable parentId) {
        Set<UserPrivilege> newUserPrivileges = new LinkedHashSet<>();
        Long parentId1 = (Long) parentId;
        boolean isAll = true;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (display != null) {
                isAll = (boolean) display == userPrivilege.getDisplay();
            }
            if (parentId1.equals(userPrivilege.getParentId()) && isAll) {
                UserPrivilege newUserPrivilege = new UserPrivilege();
                newUserPrivilege.setId(userPrivilege.getId());
                newUserPrivilege.setName(userPrivilege.getName());
                newUserPrivilege.setConstraintAuth(userPrivilege.getConstraintAuth());
                newUserPrivilege.setParentId(userPrivilege.getParentId());
                newUserPrivilege.setIconName(userPrivilege.getIconName());
                newUserPrivilege.setDisplay(userPrivilege.getDisplay());
                newUserPrivilege.setOrders(userPrivilege.getOrders());
                newUserPrivilege.setUri(userPrivilege.getUri());
                newUserPrivilege.setSubUserPrivileges(getAndSubPrivileges(userPrivileges, display, userPrivilege.getId()));
                newUserPrivileges.add(newUserPrivilege);
            }
        }
        return newUserPrivileges;
    }

    /**
     * 有序排列用户权限
     *
     * @param newUserPrivileges 排列后的系统用户权限对象集合
     * @param userPrivileges    需要排列的系统用户权限对象集合
     * @param parentId          父级编号
     * @param level             层数，显示缩进
     */
    private void getAndSubPrivileges(List<UserPrivilege> newUserPrivileges, List<UserPrivilege> userPrivileges, Serializable parentId, Integer level) {
        Long parentId1 = (Long) parentId;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (parentId1.equals(userPrivilege.getParentId())) {
                userPrivilege.setLevel(level);
                newUserPrivileges.add(userPrivilege);
                getAndSubPrivileges(newUserPrivileges, userPrivileges, userPrivilege.getId(), level + 1);
            }
        }
    }
}
