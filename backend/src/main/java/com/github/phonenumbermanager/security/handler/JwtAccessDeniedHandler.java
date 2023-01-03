package com.github.phonenumbermanager.security.handler;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.util.R;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 访问被拒绝处理器
 *
 * @author 廿二月的天
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
        throws IOException {
        R result =
            R.error(ExceptionCode.FORBIDDEN_EXCEPTION.getCode(), ExceptionCode.FORBIDDEN_EXCEPTION.getDescription())
                .put("exception", e);
        String jsonStr = JSONUtil.toJsonStr(result);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.flush();
    }
}
