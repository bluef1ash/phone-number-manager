package com.github.phonenumbermanager.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.GeetestLibUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;

/**
 * 验证码过滤器
 *
 * @author 廿二月的天
 */
public class CaptchaValidFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = httpServletRequest.getSession();
        SystemUser systemUser = (SystemUser)session.getAttribute("systemUser");
        String loginUri = "/login";
        String requestUri = httpServletRequest.getRequestURI();
        if (systemUser == null && !requestUri.contains(loginUri)) {
            httpServletResponse.sendRedirect(loginUri);
        }
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        String challenge = httpServletRequest.getParameter(GeetestLibUtil.FN_GEETEST_CHALLENGE);
        String validate = httpServletRequest.getParameter(GeetestLibUtil.FN_GEETEST_VALIDATE);
        String secCode = httpServletRequest.getParameter(GeetestLibUtil.FN_GEETEST_SECCODE);
        if (StrUtil.isEmpty(challenge) || StrUtil.isEmpty(validate) || StrUtil.isEmpty(secCode)) {
            FilterInvocation filterInvocation =
                new FilterInvocation(httpServletRequest, httpServletResponse, filterChain);
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        }
        int gtServerStatusCode = (Integer)httpServletRequest.getSession().getAttribute(gtSdk.gtServerStatusSessionKey);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", httpServletRequest.getParameter("browserType"));
        param.put("ip_address", ServletUtil.getClientIP(httpServletRequest));
        int gtResult;
        if (gtServerStatusCode == 1) {
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, secCode, param);
        } else {
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, secCode);
        }
        if (gtResult == 1) {
            FilterInvocation filterInvocation =
                new FilterInvocation(httpServletRequest, httpServletResponse, filterChain);
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } else {
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("text/json; charset=utf-8");
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 0);
            returnObj.put("fieldName", "captcha");
            returnObj.put("message", "图形验证码验证失败！");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(returnObj.toStringPretty());
            writer.flush();
        }
    }
}
