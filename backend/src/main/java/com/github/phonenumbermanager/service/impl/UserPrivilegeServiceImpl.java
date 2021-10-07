package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.mapper.RolePrivilegeRelationMapper;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.mapper.UserPrivilegeMapper;
import com.github.phonenumbermanager.service.UserPrivilegeService;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userPrivilegeService")
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilegeMapper, UserPrivilege>
    implements UserPrivilegeService {
    @Resource
    private RolePrivilegeRelationMapper rolePrivilegeRelationMapper;
    @Resource
    private SystemUserMapper systemUserMapper;

    @Override
    public Set<UserPrivilege> getByRoleId(Serializable roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    @Override
    public Set<UserPrivilege> get(Boolean display, Set<UserPrivilege> userPrivileges) {
        return getAndSubPrivileges(userPrivileges, display, 0L);
    }

    @Override
    public Set<UserPrivilege> getForSub() {
        Set<UserPrivilege> userPrivileges = new HashSet<>(baseMapper.selectList(null));
        return getAndSubPrivileges(userPrivileges, null, 0L);
    }

    @Override
    public LinkedList<UserPrivilege> getAndHandler() {
        List<UserPrivilege> userPrivileges = baseMapper.selectList(null);
        LinkedList<UserPrivilege> privileges = new LinkedList<>();
        getAndSubPrivileges(privileges, userPrivileges, 0L, 0);
        for (UserPrivilege userPrivilege : privileges) {
            String name = "|" + "  ".repeat(Math.max(0, userPrivilege.getLevel())) + "┗";
            userPrivilege.setName(name + userPrivilege.getName());
        }
        return privileges;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Serializable id) {
        QueryWrapper<RolePrivilegeRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("privilege_id", id);
        return baseMapper.deleteById(id) > 0 && rolePrivilegeRelationMapper.delete(wrapper) > 0;
    }

    /**
     * 有序排列用户权限
     *
     * @param userPrivileges
     *            需要排列的系统用户权限对象集合
     * @param display
     *            是否在导航栏中显示
     * @return 排列成功的统用户权限对象集合
     */
    private Set<UserPrivilege> getAndSubPrivileges(Set<UserPrivilege> userPrivileges, Boolean display,
        Serializable parentId) {
        Set<UserPrivilege> newUserPrivileges = new LinkedHashSet<>();
        Long parentId1 = (Long)parentId;
        boolean isAll = true;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (display != null) {
                isAll = (boolean)display == userPrivilege.getIsDisplay();
            }
            if (parentId1.equals(userPrivilege.getParentId()) && isAll) {
                UserPrivilege newUserPrivilege = new UserPrivilege();
                newUserPrivilege.setId(userPrivilege.getId());
                newUserPrivilege.setName(userPrivilege.getName());
                newUserPrivilege.setDescription(userPrivilege.getDescription());
                newUserPrivilege.setParentId(userPrivilege.getParentId());
                newUserPrivilege.setIconName(userPrivilege.getIconName());
                newUserPrivilege.setIsDisplay(userPrivilege.getIsDisplay());
                newUserPrivilege.setOrders(userPrivilege.getOrders());
                newUserPrivilege.setUri(userPrivilege.getUri());
                newUserPrivilege
                    .setSubUserPrivileges(getAndSubPrivileges(userPrivileges, display, userPrivilege.getId()));
                newUserPrivileges.add(newUserPrivilege);
            }
        }
        return newUserPrivileges;
    }

    /**
     * 有序排列用户权限
     *
     * @param newUserPrivileges
     *            排列后的系统用户权限对象集合
     * @param userPrivileges
     *            需要排列的系统用户权限对象集合
     * @param parentId
     *            父级编号
     * @param level
     *            层数，显示缩进
     */
    private void getAndSubPrivileges(List<UserPrivilege> newUserPrivileges, List<UserPrivilege> userPrivileges,
        Serializable parentId, Integer level) {
        Long parentId1 = (Long)parentId;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (parentId1.equals(userPrivilege.getParentId())) {
                userPrivilege.setLevel(level);
                newUserPrivileges.add(userPrivilege);
                getAndSubPrivileges(newUserPrivileges, userPrivileges, userPrivilege.getId(), level + 1);
            }
        }
    }
}
