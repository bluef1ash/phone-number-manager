package com.github.phonenumbermanager.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.util.CommonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 访问被拒绝处理
 *
 * @author 廿二月的天
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        String errorMessage = e.getMessage();
        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute(CsrfToken.class.getName());
        String token = csrfToken.getToken();
        e.printStackTrace();
        if (e instanceof MissingCsrfTokenException || e instanceof InvalidCsrfTokenException) {
            errorMessage = "CSRF验证失败，请刷新后再试！";
        }
        if (CommonUtil.isRequestAjax(httpServletRequest)) {
            httpServletResponse.setContentType("text/json; charset=utf-8");
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 0);
            returnObj.put("message", errorMessage);
            returnObj.put("csrf", token);
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(returnObj.toJSONString());
            writer.flush();
        } else {
            httpServletRequest.setAttribute("errorMessage", errorMessage);
            httpServletResponse.sendError(403);
        }
    }
}
