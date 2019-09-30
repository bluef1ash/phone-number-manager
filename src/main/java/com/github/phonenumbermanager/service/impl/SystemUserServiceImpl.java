package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;

/**
 * 系统用户Service实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUser> implements SystemUserService, UserDetailsService {

    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserDao.selectAndRoleAndPrivilegesByName(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException(username + "系统用户不存在！");
        }
        List<Configuration> configurations = configurationDao.selectAll();
        Map<String, Object> configurationsMap = new HashMap<>(configurations.size() + 1);
        for (Configuration configuration : configurations) {
            configurationsMap.put(configuration.getKey(), configuration.getValue());
        }
        Integer systemIsActive = CommonUtils.convertConfigurationInteger(configurationsMap.get("system_is_active"));
        Long systemAdministratorId = CommonUtils.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        if (systemIsActive == 0 && !systemAdministratorId.equals(systemUser.getId())) {
            throw new LockedException("该系统已经禁止登录，请联系管理员！");
        }
        // 系统用户权限
        Set<UserPrivilege> userPrivilegesAll = new LinkedHashSet<>(userPrivilegeDao.selectAll());
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        if (systemAdministratorId.equals(systemUser.getId())) {
            userPrivileges = userPrivilegesAll;
        } else {
            Set<UserPrivilege> privileges = systemUser.getUserRole().getUserPrivileges();
            for (UserPrivilege privilege : privileges) {
                userPrivileges.add(privilege);
                userPrivileges.addAll(recursionPrivileges(userPrivilegesAll, privilege.getId()));
            }
            systemUser.getUserRole().setUserPrivileges(userPrivileges);
        }
        HttpSession session = request.getSession();
        session.setAttribute("systemUser", systemUser);
        session.setAttribute("userPrivileges", userPrivileges);
        session.setAttribute("configurationsMap", configurationsMap);
        return systemUser;
    }

    @Override
    public long create(SystemUser systemUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        systemUser.setLoginTime(DateUtils.getTimestamp(new Date(0)));
        systemUser.setCreateTime(DateUtils.getTimestamp(new Date()));
        systemUser.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(systemUser);
    }

    @Override
    public long update(SystemUser systemUser) {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        systemUser.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(systemUser);
    }

    @Override
    public Map<String, Object> findCorrelation(Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<SystemUser> data = systemUserDao.selectAndRoles();
        return find(data);
    }

    @Override
    public SystemUser findCorrelation(Serializable id) {
        return systemUserDao.selectAndRoleById(id);
    }

    @Override
    public List<SystemUser> findIdAndName() {
        return systemUserDao.selectIdAndName();
    }

    /**
     * 递归系统用户权限
     *
     * @param userPrivilegesAll 全部的系统权限
     * @param parentId          上级系统权限编号
     * @return 处理后的系统权限集合
     */
    private Set<UserPrivilege> recursionPrivileges(Set<UserPrivilege> userPrivilegesAll, Long parentId) {
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        for (UserPrivilege userPrivilege : userPrivilegesAll) {
            if (parentId.equals(userPrivilege.getParentId())) {
                userPrivileges.add(userPrivilege);
                userPrivileges.addAll(recursionPrivileges(userPrivilegesAll, userPrivilege.getId()));
            }
        }
        return userPrivileges;
    }
}
