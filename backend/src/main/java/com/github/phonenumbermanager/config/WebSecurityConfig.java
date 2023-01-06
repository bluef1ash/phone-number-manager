package com.github.phonenumbermanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.security.JwtAuthenticationEntryPoint;
import com.github.phonenumbermanager.security.filter.JwtTokenFilter;
import com.github.phonenumbermanager.security.handler.JwtAccessDeniedHandler;
import com.github.phonenumbermanager.security.manager.AuthorizationManager;

import lombok.AllArgsConstructor;

/**
 * Spring Security配置
 *
 * @author 廿二月的天
 */
@Configuration
@AllArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {
    private final AuthorizationManager authorizationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable().addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler).and().headers().frameOptions().sameOrigin().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(request -> request.requestMatchers(SystemConstant.PERMISSION_WHITELIST).permitAll()
                .requestMatchers(SystemConstant.ANONYMOUS_WHITELIST).anonymous().anyRequest()
                .access(authorizationManager))
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**")
            .requestMatchers(SystemConstant.PERMISSION_WHITELIST);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
