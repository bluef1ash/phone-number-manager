package com.github.phonenumbermanager.security.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CSRF过滤器
 *
 * @author 廿二月的天
 */
public class CsrfFilter extends OncePerRequestFilter implements Filter {

    @Value("${custom.debug}")
    private Boolean debug;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            Cookie cookie = WebUtils.getCookie(httpServletRequest, "X-CSRF-TOKEN");
            String token = csrfToken.getToken();
            boolean isToken = cookie == null || token != null && !token.equals(cookie.getValue());
            if (isToken) {
                cookie = new Cookie("X-CSRF-TOKEN", token);
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
        }
        debug = debug != null && debug;
        httpServletRequest.setAttribute("debug", debug);
        FilterInvocation filterInvocation = new FilterInvocation(httpServletRequest, httpServletResponse, filterChain);
        filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
    }
}
