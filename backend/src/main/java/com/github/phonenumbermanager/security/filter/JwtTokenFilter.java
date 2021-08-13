package com.github.phonenumbermanager.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;

import cn.hutool.jwt.JWTUtil;

/**
 * JWTToken拦截器
 *
 * @author 廿二月的天
 */
public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String bearerToken = request.getHeader(SystemConstant.HEADER_STRING);
        String jwtToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SystemConstant.TOKEN_PREFIX)) {
            jwtToken = bearerToken.substring(SystemConstant.TOKEN_PREFIX.length());
        }
        JWTUtil jwtUtil = new JWTUtil();
        if (StringUtils.hasText(jwtToken)
            && jwtUtil.verify(jwtToken, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8))) {
            String username = (String)jwtUtil.parseToken(jwtToken).getPayload(SystemConstant.USERNAME_KEY);
            String authority = (String)jwtUtil.parseToken(jwtToken).getPayload(SystemConstant.AUTHORITIES_KEY);
            Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authority.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername(username).setAuthorities(authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(systemUser, jwtToken, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
