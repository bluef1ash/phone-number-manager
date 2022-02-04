package com.github.phonenumbermanager.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;

/**
 * JWTToken拦截器
 *
 * @author 廿二月的天
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String bearerToken = request.getHeader(SystemConstant.HEADER_STRING);
        String jwtToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SystemConstant.TOKEN_PREFIX)) {
            jwtToken = bearerToken.substring(SystemConstant.TOKEN_PREFIX.length());
        }
        if (StringUtils.hasText(jwtToken)
            && JWTUtil.verify(jwtToken, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8))) {
            Long systemUserId = (Long)JWTUtil.parseToken(jwtToken).getPayload(SystemConstant.SYSTEM_USER_ID_KEY);
            SystemUser systemUser = JSONUtil
                .toBean((String)redisUtil.get(SystemConstant.SYSTEM_USER_ID_KEY + systemUserId), SystemUser.class);
            Authentication authentication =
                new UsernamePasswordAuthenticationToken(systemUser, jwtToken, systemUser.getAuthorities());
            redisUtil.expire(SystemConstant.SYSTEM_USER_ID_KEY + systemUserId,
                LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.of("+8")), TimeUnit.SECONDS);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
