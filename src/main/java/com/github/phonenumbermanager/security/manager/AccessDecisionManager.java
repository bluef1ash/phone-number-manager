package com.github.phonenumbermanager.security.manager;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.util.CommonUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;

/**
 * 访问控制决策
 *
 * @author 廿二月的天
 */
public class AccessDecisionManager implements org.springframework.security.access.AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        Object principal = authentication.getPrincipal();
        HttpSession session = filterInvocation.getRequest().getSession();
        Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
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
            for (int i = 0; i < SystemConstant.LOGGED_PERMITS.length; i++) {
                if (configAttribute.getAttribute().equals(SystemConstant.LOGGED_PERMITS[i])) {
                    return;
                }
            }
            for (UserPrivilege userPrivilege : systemUser.getUserRole().getUserPrivileges()) {
                if (configAttribute.getAttribute().equals(userPrivilege.getUri())) {
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
