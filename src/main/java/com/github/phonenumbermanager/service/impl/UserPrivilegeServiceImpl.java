package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRolePrivilege;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import com.github.phonenumbermanager.utils.DateUtils;
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
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilege> implements UserPrivilegeService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long create(UserPrivilege userPrivilege) {
        userPrivilege.setCreateTime(DateUtils.getTimestamp(new Date()));
        userPrivilege.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(userPrivilege);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long update(UserPrivilege userPrivilege) {
        userPrivilege.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(userPrivilege);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public Set<UserPrivilege> findByRoleId(Serializable roleId) {
        return userPrivilegeDao.selectByRoleId(roleId);
    }

    @Override
    public Set<UserPrivilege> find(Boolean display, Set<UserPrivilege> userPrivileges) {
        return findAndSubPrivileges(userPrivileges, display, 0L);
    }

    @Override
    public Set<UserPrivilege> findForSub() {
        Set<UserPrivilege> userPrivileges = new HashSet<>(userPrivilegeDao.selectAll());
        return findAndSubPrivileges(userPrivileges, null, 0L);
    }

    @Override
    public LinkedList<UserPrivilege> findAndHandler() {
        List<UserPrivilege> userPrivileges = userPrivilegeDao.selectAll();
        LinkedList<UserPrivilege> privileges = new LinkedList<>();
        findAndSubPrivileges(privileges, userPrivileges, 0L, 0);
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
    public long delete(Serializable id) {
        if (systemUserDao.selectByRoleId(id).size() > 0) {
            return 0;
        }
        UserRolePrivilege userRolePrivilege = new UserRolePrivilege();
        userRolePrivilege.setPrivilegeId((Long) id);
        userRolePrivilegeDao.deleteByUserRolePrivilege(userRolePrivilege);
        return super.delete(id);
    }

    /**
     * 有序排列用户权限
     *
     * @param userPrivileges 需要排列的系统用户权限对象集合
     * @param display        是否在导航栏中显示
     * @return 排列成功的统用户权限对象集合
     */
    private Set<UserPrivilege> findAndSubPrivileges(Set<UserPrivilege> userPrivileges, Boolean display, Serializable parentId) {
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
                newUserPrivilege.setSubUserPrivileges(findAndSubPrivileges(userPrivileges, display, userPrivilege.getId()));
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
    private void findAndSubPrivileges(List<UserPrivilege> newUserPrivileges, List<UserPrivilege> userPrivileges, Serializable parentId, Integer level) {
        Long parentId1 = (Long) parentId;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (parentId1.equals(userPrivilege.getParentId())) {
                userPrivilege.setLevel(level);
                newUserPrivileges.add(userPrivilege);
                findAndSubPrivileges(newUserPrivileges, userPrivileges, userPrivilege.getId(), level + 1);
            }
        }
    }
}
