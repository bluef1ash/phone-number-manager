package com.github.phonenumbermanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;

/**
 * 系统用户业务实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService, UserDetailsService {

    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserMapper.selectAndRoleAndPrivilegesByName(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException(username + "系统用户不存在！");
        }
        List<Configuration> configurations = configurationMapper.selectList(null);
        Map<String, Object> configurationsMap = new HashMap<>(configurations.size() + 1);
        for (Configuration configuration : configurations) {
            configurationsMap.put(configuration.getKey(), configuration.getValue());
        }
        Integer systemIsActive = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_is_active"));
        Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        if (systemIsActive == 0 && !systemAdministratorId.equals(systemUser.getId())) {
            throw new LockedException("该系统已经禁止登录，请联系管理员！");
        }
        // 系统用户权限
        Set<UserPrivilege> userPrivilegesAll = new LinkedHashSet<>(userPrivilegeMapper.selectList(null));
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SystemUser systemUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        systemUser.setLoginTime(new Date(0));
        return systemUserMapper.insert(systemUser) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SystemUser systemUser) {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        return systemUserMapper.updateById(systemUser) > 0;
    }

    @Override
    public IPage<SystemUser> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        Page<SystemUser> page = new Page<>(pageNumber, pageDataSize);
        return systemUserMapper.selectAndRoles(page);
    }

    @Override
    public SystemUser getCorrelation(Serializable id) {
        return systemUserMapper.selectAndRoleById(id);
    }

    @Override
    public List<SystemUser> getIdAndName() {
        return systemUserMapper.selectIdAndName();
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
