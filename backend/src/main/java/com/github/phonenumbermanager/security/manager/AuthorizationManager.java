package com.github.phonenumbermanager.security.manager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 授权管理器
 *
 * @author 廿二月的天
 */
@Component
public class AuthorizationManager
    implements org.springframework.security.authorization.AuthorizationManager<RequestAuthorizationContext> {
    private final ConfigurationService configurationService;

    @Autowired
    public AuthorizationManager(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
        RequestAuthorizationContext requestAuthorizationContext) {
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        Authentication auth = authentication.get();
        boolean hasPermission = false;
        if (!SystemConstant.ANONYMOUS_USER.equals(auth.getPrincipal())) {
            String uri = request.getRequestURI();
            SystemUser systemUser = (SystemUser)auth.getPrincipal();
            Map<String, JSONObject> configurationMap = configurationService.mapAll();
            if (ArrayUtil.contains(SystemConstant.PERMISSION_WHITELIST, uri) || systemUser.getId()
                .equals(Long.valueOf((String)configurationMap.get("system_administrator_id").get("content")))) {
                hasPermission = true;
            }
            HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
            @SuppressWarnings("all")
            List<Company> companies = (List<Company>)auth.getAuthorities();
            if (companies != null && !companies.isEmpty()) {
                AntPathMatcher antPathMatcher = new AntPathMatcher();
                hasPermission = companies.stream().flatMap(company -> company.getSystemPermissions().stream())
                    .anyMatch(systemPermission -> antPathMatcher.match(systemPermission.getUri(), uri)
                        && Arrays.asList(systemPermission.getHttpMethods()).contains(method));
            }
        }
        return new AuthorizationDecision(hasPermission);

    }
}
