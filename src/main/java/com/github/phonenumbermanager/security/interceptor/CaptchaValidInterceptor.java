package com.github.phonenumbermanager.security.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.GeetestLibUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码拦截器
 *
 * @author 廿二月的天
 */
public class CaptchaValidInterceptor implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        String loginUri = "/login";
        String requestUri = httpServletRequest.getRequestURI();
        boolean isPermit = false;
        for (int i = 0; i < SystemConstant.LOGGED_PERMITS.length; i++) {
            if (requestUri.contains(SystemConstant.LOGGED_PERMITS[i])) {
                isPermit = true;
                break;
            }
        }
        if (systemUser == null && !requestUri.contains(loginUri) && !isPermit) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect(loginUri);
        }
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        String challenge = request.getParameter(GeetestLibUtil.FN_GEETEST_CHALLENGE);
        String validate = request.getParameter(GeetestLibUtil.FN_GEETEST_VALIDATE);
        String secCode = request.getParameter(GeetestLibUtil.FN_GEETEST_SECCODE);
        if (StringUtils.isEmpty(challenge) || StringUtils.isEmpty(validate) || StringUtils.isEmpty(secCode)) {
            FilterInvocation filterInvocation = new FilterInvocation(request, response, filterChain);
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        }
        int gtServerStatusCode = (Integer) httpServletRequest.getSession().getAttribute(gtSdk.gtServerStatusSessionKey);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", request.getParameter("browserType"));
        param.put("ip_address", CommonUtil.getIp(httpServletRequest));
        int gtResult;
        if (gtServerStatusCode == 1) {
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, secCode, param);
        } else {
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, secCode);
        }
        if (gtResult == 1) {
            FilterInvocation filterInvocation = new FilterInvocation(request, response, filterChain);
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } else {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("text/json; charset=utf-8");
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 0);
            returnObj.put("fieldName", "captcha");
            returnObj.put("message", "图形验证码验证失败！");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(returnObj.toJSONString());
            writer.flush();
        }

    }
}
