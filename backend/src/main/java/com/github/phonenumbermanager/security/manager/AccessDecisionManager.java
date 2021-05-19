package com.github.phonenumbermanager.security.manager;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.RedisUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

/**
 * 访问控制决策
 *
 * @author 廿二月的天
 */
public class AccessDecisionManager implements org.springframework.security.access.AccessDecisionManager {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if (redisUtil == null) {
            return;
        }
        Object principal = authentication.getPrincipal();
        @SuppressWarnings("all") Map<String, Object> configurationsMap = (Map<String, Object>) redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        if (!(principal instanceof UserDetails) || configurationsMap == null) {
            return;
        }
        SystemUser systemUser = (SystemUser) principal;
        Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        if (systemAdministratorId.equals(systemUser.getId())) {
            return;
        }
        for (ConfigAttribute configAttribute : configAttributes) {
            for (int i = 0; i < SystemConstant.PRIVILEGE_PERMITS.length; i++) {
                if (configAttribute.getAttribute().equals(SystemConstant.PRIVILEGE_PERMITS[i])) {
                    return;
                }
            }

            for (GrantedAuthority authority : systemUser.getAuthorities()) {
                if (configAttribute.getAttribute().equals(authority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("您没有该权限，请联系上级管理用户！");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
