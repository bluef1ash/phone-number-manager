package com.github.phonenumbermanager.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.util.CommonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户登录认证失败后执行的操作
 *
 * @author 廿二月的天
 */
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException {
        StringBuilder exceptionMessage = new StringBuilder();
        exceptionMessage.append("登录失败！");
        String fieldName = null;
        if (exception instanceof BadCredentialsException) {
            exceptionMessage.append("登录密码输入错误，请重新输入！");
            fieldName = "password";
        } else if (exception instanceof UsernameNotFoundException) {
            exceptionMessage.append(exception.getMessage());
            fieldName = "username";
        } else if (exception instanceof LockedException) {
            exceptionMessage.append("该系统用户已被锁定，不允许登录，请联系管理员！");
            fieldName = "username";
        } else if (exception instanceof DisabledException) {
            exceptionMessage.append("该系统用户已被禁用！");
            fieldName = "username";
        } else if (exception instanceof CredentialsExpiredException) {
            exceptionMessage.append("密码已过期，请联系管理员更换密码！");
            fieldName = "password";
        }
        httpServletRequest.getSession().invalidate();
        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute(CsrfToken.class.getName());
        httpServletResponse.setCharacterEncoding("UTF-8");
        if (CommonUtil.isRequestAjax(httpServletRequest)) {
            httpServletResponse.setContentType("text/json; charset=utf-8");
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 0);
            returnObj.put("fieldName", fieldName);
            returnObj.put("message", exceptionMessage.toString());
            returnObj.put("csrf", csrfToken.getToken());
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(returnObj.toJSONString());
            writer.flush();
        } else {
            throw new BusinessException(exceptionMessage.toString());
        }
    }
}
