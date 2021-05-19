package com.github.phonenumbermanager.security.filter;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWTToken拦截器
 *
 * @author 廿二月的天
 */
public class JwtTokenFilter extends OncePerRequestFilter {
    private SystemUserService systemUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(SystemConstant.HEADER_STRING);
        if (authHeader != null && authHeader.startsWith(SystemConstant.TOKEN_PREFIX)) {
            final String authToken = authHeader.substring(SystemConstant.TOKEN_PREFIX.length());
            String username = JwtTokenUtil.getUsernameFromToken(authToken);
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = systemUserService.loadUserByUsername(username);
                if (JwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

    public void setSystemUserService(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }
}
