package com.github.phonenumbermanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.JwtTokenUtil;
import com.github.phonenumbermanager.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * 系统用户业务实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = systemUserMapper.selectAndRoleAndPrivilegesByName(username);
        @SuppressWarnings("all") Map<String, Object> configurationsMap = (Map<String, Object>) redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        // 系统用户权限
        Set<UserPrivilege> userPrivilegesAll = new LinkedHashSet<>(userPrivilegeMapper.selectList(null));
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        if (systemAdministratorId.equals(systemUser.getId())) {
            userPrivileges.addAll(userPrivilegesAll);
            for (UserRole userRole : systemUser.getUserRoles()) {
                userRole.setUserPrivileges(userPrivileges);
            }
        } else {
            for (UserRole userRole : systemUser.getUserRoles()) {
                Set<UserPrivilege> privileges = userRole.getUserPrivileges();
                for (UserPrivilege privilege : privileges) {
                    userPrivileges.add(privilege);
                    userPrivileges.addAll(recursionPrivileges(userPrivilegesAll, privilege.getId()));
                }
                userRole.setUserPrivileges(userPrivileges);
            }
        }
        String[] uri = userPrivileges.stream().map(UserPrivilege::getUri).toArray(String[]::new);
        systemUser.setAuthorities(AuthorityUtils.createAuthorityList(uri));
        return systemUser;
    }

    @Override
    public boolean authentication(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return true;
    }

    @Override
    public String refreshToken(String token) {
        if (!JwtTokenUtil.canTokenBeRefreshed(token)) {
            token = JwtTokenUtil.refreshToken(token);
        }
        return token;
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

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            SystemUser systemUser = (SystemUser) authentication.getPrincipal();
            if (systemUser != null) {
                return systemUser.getAuthorities().contains(new SimpleGrantedAuthority(request.getRequestURI()));
            }
        }
        return false;
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
