package com.github.phonenumbermanager.security;

import com.github.phonenumbermanager.util.CommonUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录异常处理
 *
 * @author 廿二月的天
 */
public class LoginAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    private final String loginFormUrl;

    public LoginAuthenticationEntryPoint(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        if (CommonUtil.isRequestAjax(httpServletRequest)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } else {
            new LoginUrlAuthenticationEntryPoint(loginFormUrl);
        }

    }
}
