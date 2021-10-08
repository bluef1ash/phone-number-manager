package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRoleRelation;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.mapper.UserRoleRelationMapper;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统用户业务实现
 *
 * @author 廿二月的天
 */
@Slf4j
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserRoleRelationMapper userRoleRelationMapper;
    @Resource
    private ConfigurationService configurationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = baseMapper.selectAndRolesByName(username);
        @SuppressWarnings("all")
        Map<String, Object> configurationsMap =
            (Map<String, Object>)redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        if (configurationsMap == null) {
            List<Configuration> configurations = configurationService.list(null);
            configurationsMap = new HashMap<>(configurations.size() + 1);
            for (Configuration configuration : configurations) {
                configurationsMap.put(configuration.getKey(), configuration.getValue());
            }
            redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, configurationsMap);
        }
        String systemAdministratorName = String.valueOf(configurationsMap.get("system_administrator_name"));
        Integer systemIsActive = Convert.toInt(configurationsMap.get("system_is_active"));
        if (systemIsActive == 0 && !systemAdministratorName.equals(systemUser.getUsername())) {
            throw new SystemClosedException("该系统已经禁止登录，请联系管理员！");
        }
        return systemUser;
    }

    @Override
    public Authentication authentication(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate =
            authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SystemUser systemUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        systemUser.setLoginTime(new Date(0));
        return baseMapper.insert(systemUser) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SystemUser systemUser) {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        return baseMapper.updateById(systemUser) > 0;
    }

    @Override
    public IPage<SystemUser> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        Page<SystemUser> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectAndRoles(page);
    }

    @Override
    public SystemUser getCorrelation(Serializable id) {
        return baseMapper.selectAndRoleById(id);
    }

    @Override
    public List<SystemUser> getIdAndName() {
        return baseMapper.selectIdAndName();
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Serializable id) {
        QueryWrapper<UserRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        return baseMapper.deleteById(id) > 0 && userRoleRelationMapper.delete(wrapper) > 0;
    }

    /**
     * 递归系统用户权限
     *
     * @param userPrivileges
     *            需要递归的系统权限
     * @param parentId
     *            上级系统权限编号
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
