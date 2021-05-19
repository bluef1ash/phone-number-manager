package com.github.phonenumbermanager.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import org.springframework.security.core.Authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 登录认证成功后执行的操作
 *
 * @author 廿二月的天
 */
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    @Resource
    private SystemUserService systemUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        if (CommonUtil.isRequestAjax(httpServletRequest)) {
            SystemUser systemUser = (SystemUser) authentication.getPrincipal();
            systemUser.setPassword(null);
            systemUser.setLoginTime(new Date());
            systemUser.setLoginIp(CommonUtil.getIp(httpServletRequest));
            if (!systemUserService.updateById(systemUser)) {
                return;
            }
            systemUser.setPassword(null);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-Type", "text/json;charset=UTF-8");
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 1);
            returnObj.put("message", "登录成功！");
            httpServletResponse.getWriter().write(returnObj.toString());
            httpServletResponse.getWriter().flush();
        }
    }
}
