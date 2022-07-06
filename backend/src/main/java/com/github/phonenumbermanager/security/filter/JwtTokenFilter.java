package com.github.phonenumbermanager.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import lombok.AllArgsConstructor;

/**
 * JWTToken拦截器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String[] whiteList = ArrayUtil.addAll(SystemConstant.PERMIT_WHITELIST, SystemConstant.ANONYMOUS_WHITELIST);
        if (ArrayUtil.contains(whiteList, request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        String bearerToken = request.getHeader(SystemConstant.HEADER_STRING);
        String jwtToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SystemConstant.TOKEN_PREFIX)) {
            jwtToken = bearerToken.substring(SystemConstant.TOKEN_PREFIX.length());
        }
        if (StringUtils.hasText(jwtToken)
            && JWTUtil.verify(jwtToken, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8))) {
            String systemUserId =
                String.valueOf(JWTUtil.parseToken(jwtToken).getPayload(SystemConstant.SYSTEM_USER_ID_KEY));
            SystemUser systemUser = JSONUtil
                .toBean((String)redisUtil.get(SystemConstant.SYSTEM_USER_ID_KEY + systemUserId), SystemUser.class);
            if (systemUser == null || systemUser.getId() == null
                || LocalDateTime.now().isAfter(systemUser.getCredentialExpireTime())) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/json; charset=utf-8");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                JSONObject returnObj = new JSONObject();
                returnObj.putOpt("code", ExceptionCode.NOT_LOGGED.getCode());
                returnObj.putOpt("message", ExceptionCode.NOT_LOGGED.getDescription());
                PrintWriter writer = response.getWriter();
                writer.write(returnObj.toStringPretty());
                writer.flush();
                return;
            }
            Authentication authentication =
                new UsernamePasswordAuthenticationToken(systemUser, jwtToken, systemUser.getAuthorities());
            redisUtil.expire(SystemConstant.SYSTEM_USER_ID_KEY + systemUserId,
                LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.of("+8")), TimeUnit.SECONDS);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
