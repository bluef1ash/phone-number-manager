package com.github.phonenumbermanager.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.HttpMethodEnum;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.RedisUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = systemUserMapper.selectAndRolesByName(username);
        @SuppressWarnings("all") Map<String, Object> configurationsMap = (Map<String, Object>) redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        Long systemAdministratorId = Convert.toLong(configurationsMap.get("system_administrator_id"));
        // 系统用户权限
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Set<UserPrivilege> userPrivileges = null;
        if (systemAdministratorId.equals(systemUser.getId())) {
            userPrivileges = new LinkedHashSet<>(userPrivilegeMapper.selectList(null));
            for (UserPrivilege userPrivilege : userPrivileges) {
                if (StringUtils.isNotEmpty(userPrivilege.getUri())) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(userPrivilege.getUri() + "|" + HttpMethodEnum.ALL.getDescription()));
                }
            }
        }
        QueryWrapper<RolePrivilegeRelation> wrapper = new QueryWrapper<>();
        for (UserRole userRole : systemUser.getUserRoles()) {
            if (!systemAdministratorId.equals(systemUser.getId())) {
                userPrivileges = userPrivilegeMapper.selectByRoleId(userRole.getId());
                wrapper.eq("role_id", userRole.getId());
                List<RolePrivilegeRelation> rolePrivilegeRelations = rolePrivilegeRelationMapper.selectList(wrapper);
                for (UserPrivilege userPrivilege : userPrivileges) {
                    for (RolePrivilegeRelation rolePrivilegeRelation : rolePrivilegeRelations) {
                        if (userPrivilege.getId().equals(rolePrivilegeRelation.getPrivilegeId()) && StringUtils.isNotEmpty(userPrivilege.getUri())) {
                            grantedAuthorities.add(new SimpleGrantedAuthority(userPrivilege.getUri() + "|" + rolePrivilegeRelation.getMethod().getDescription()));
                        }
                    }
                }
            }
            assert userPrivileges != null;
            userRole.setUserPrivileges(recursionPrivileges(userPrivileges, 0L));
        }
        systemUser.setAuthorities(grantedAuthorities);
        return systemUser;
    }

    @Override
    public Authentication authentication(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate;
        try {
            authenticate = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
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
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (ArrayUtils.contains(SystemConstant.PRIVILEGE_PERMITS, uri)) {
            return true;
        }
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null && authorities.size() > 0) {
                for (GrantedAuthority authority : authorities) {
                    String authorityUri = authority.getAuthority();
                    String authorityMethod = null;
                    int indexOf = authorityUri.indexOf("|");
                    if (indexOf > 0) {
                        authorityUri = authorityUri.substring(0, indexOf - 1);
                        authorityMethod = authorityUri.substring(indexOf);
                    }
                    if (authorityUri.equals(uri)) {
                        if (authorityMethod != null && authorityMethod.indexOf(",") > 0) {
                            return ArrayUtils.contains(authorityMethod.split(","), method);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 递归系统用户权限
     *
     * @param userPrivileges 需要递归的系统权限
     * @param parentId       上级系统权限编号
     * @return 处理后的系统权限集合
     */
    private Set<UserPrivilege> recursionPrivileges(Set<UserPrivilege> userPrivileges, Long parentId) {
        Set<UserPrivilege> userPrivilegeList = new LinkedHashSet<>();
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (parentId.equals(userPrivilege.getParentId())) {
                userPrivilege.setSubUserPrivileges(recursionPrivileges(userPrivileges, userPrivilege.getId()));
                userPrivilegeList.add(userPrivilege);
            }
        }
        return userPrivilegeList;
    }
}
