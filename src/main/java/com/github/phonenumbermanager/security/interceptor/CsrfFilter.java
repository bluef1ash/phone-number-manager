package com.github.phonenumbermanager.security.interceptor;

import com.github.phonenumbermanager.utils.CommonUtils;
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
            if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                cookie = new Cookie("X-CSRF-TOKEN", token);
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
        }
        debug = debug == null ? false : debug;
        httpServletRequest.setAttribute("debug", debug);
        if (debug && !CommonUtils.isRequestAjax(httpServletRequest)) {
            String[] paths = httpServletRequest.getServletPath().split("/");
            boolean is2Path = false;
            if (paths.length == 0) {
                paths = new String[]{"index"};
            } else {
                is2Path = "create".equals(paths[paths.length - 1]) || "list".equals(paths[paths.length - 1]) || "edit".equals(paths[paths.length - 1]) || "handle".equals(paths[paths.length - 1]);
            }
            StringBuilder filePath = new StringBuilder();
            filePath.append("http://127.0.0.1:3000/");
            if (is2Path) {
                filePath.append(paths[paths.length - 2]).append("-");
                paths[paths.length - 1] = paths[paths.length - 1].replaceAll("create|handle", "edit");
            }
            filePath.append(paths[paths.length - 1]).append("-bundle.js");
            httpServletRequest.setAttribute("jsFilePath", filePath);
        }
        FilterInvocation filterInvocation = new FilterInvocation(httpServletRequest, httpServletResponse, filterChain);
        filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
    }
}
