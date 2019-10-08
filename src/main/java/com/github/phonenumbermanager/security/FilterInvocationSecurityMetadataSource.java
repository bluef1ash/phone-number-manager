package com.github.phonenumbermanager.security;

import com.github.phonenumbermanager.constant.SecurityConfigType;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.UserPrivilege;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 加载验证权限数据
 *
 * @author 廿二月的天
 */
public class FilterInvocationSecurityMetadataSource implements org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource {

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpSession session = filterInvocation.getHttpRequest().getSession();
        Set<UserPrivilege> userPrivileges = (Set<UserPrivilege>) session.getAttribute("userPrivileges");
        List<ConfigAttribute> configAttributes = new ArrayList<>();
        if (userPrivileges != null) {
            String url = filterInvocation.getRequestUrl();
            int firstQuestionMarkIndex = url.indexOf("?");
            if (firstQuestionMarkIndex != -1) {
                url = url.substring(0, firstQuestionMarkIndex);
            }
            for (UserPrivilege userPrivilege : userPrivileges) {
                if (url.equals(userPrivilege.getUri())) {
                    SecurityConfig securityConfig = new SecurityConfig(userPrivilege.getUri());
                    configAttributes.add(securityConfig);
                }
            }
            for (int i = 0; i < SystemConstant.PRIVILEGE_PERMITS.length; i++) {
                if (SystemConstant.PRIVILEGE_PERMITS[i].equalsIgnoreCase(url)) {
                    SecurityConfig securityConfig = new SecurityConfig(url);
                    configAttributes.add(securityConfig);
                }
            }
            if (configAttributes.size() == 0) {
                SecurityConfig securityConfig = new SecurityConfig(SecurityConfigType.NOT_MATCH.toString());
                configAttributes.add(securityConfig);
            }
        } else {
            configAttributes.add(new SecurityConfig(SecurityConfigType.NOT_LOGGED.toString()));
        }
        return configAttributes;
    }


    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
