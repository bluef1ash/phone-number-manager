package com.github.phonenumbermanager.config;

import com.github.phonenumbermanager.security.FilterInvocationSecurityMetadataSource;
import com.github.phonenumbermanager.security.SimpleRedirectSessionInformationExpiredStrategy;
import com.github.phonenumbermanager.security.handler.AccessDeniedHandlerImpl;
import com.github.phonenumbermanager.security.handler.AuthenticationFailureHandler;
import com.github.phonenumbermanager.security.handler.AuthenticationSuccessHandler;
import com.github.phonenumbermanager.security.interceptor.CaptchaValidInterceptor;
import com.github.phonenumbermanager.security.interceptor.CsrfFilter;
import com.github.phonenumbermanager.security.manager.AccessDecisionManager;
import com.github.phonenumbermanager.service.impl.SystemUserServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security配置
 *
 * @author 廿二月的天
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_FROM_URL = "/login";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login", "/login/**", "/getcsrf", "/error").permitAll().and().authorizeRequests().anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_FROM_URL)).accessDeniedHandler(accessDeniedHandler()).and().formLogin().loginPage(LOGIN_FROM_URL).loginProcessingUrl("/login/ajax").usernameParameter("username").passwordParameter("password").successHandler(authenticationSuccessHandler()).failureHandler(authenticationFailureHandler()).and().logout().logoutUrl("/login/logout").logoutSuccessUrl(LOGIN_FROM_URL).invalidateHttpSession(true).deleteCookies("JSESSIONID").and().sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy()).and().addFilterAt(concurrentSessionFilter(), ConcurrentSessionFilter.class).addFilterBefore(captchaValidInterceptor(), UsernamePasswordAuthenticationFilter.class).addFilterBefore(securityFilter(), org.springframework.security.web.access.intercept.FilterSecurityInterceptor.class).addFilterAfter(csrfFilter(), org.springframework.security.web.csrf.CsrfFilter.class).requestCache().requestCache(new HttpSessionRequestCache()).and().headers().frameOptions().disable().and().csrf();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(systemUserServiceImpl()).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/**", "/javascript/**", "/fonts/**", "/images/**", "/uploads/**");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        AuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler();
        authenticationSuccessHandler.setSystemUserService(systemUserServiceImpl());
        return authenticationSuccessHandler;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy() {
        SessionRegistry sessionRegistry = sessionRegistry();
        ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrentSessionControlAuthenticationStrategy.setExceptionIfMaximumExceeded(false);
        concurrentSessionControlAuthenticationStrategy.setMaximumSessions(1);
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
        RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy = new RegisterSessionAuthenticationStrategy(sessionRegistry);
        List<SessionAuthenticationStrategy> sessionAuthenticationStrategies = new ArrayList<>();
        sessionAuthenticationStrategies.add(concurrentSessionControlAuthenticationStrategy);
        sessionAuthenticationStrategies.add(sessionFixationProtectionStrategy);
        sessionAuthenticationStrategies.add(registerSessionAuthenticationStrategy);
        return new CompositeSessionAuthenticationStrategy(sessionAuthenticationStrategies);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter() {
        SessionRegistry sessionRegistry = sessionRegistry();
        SimpleRedirectSessionInformationExpiredStrategy sessionInformationExpiredStrategy = new SimpleRedirectSessionInformationExpiredStrategy(LOGIN_FROM_URL);
        return new ConcurrentSessionFilter(sessionRegistry, sessionInformationExpiredStrategy);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SystemUserServiceImpl systemUserServiceImpl() {
        return new SystemUserServiceImpl();
    }

    private CaptchaValidInterceptor captchaValidInterceptor() {
        return new CaptchaValidInterceptor();
    }

    private FilterSecurityInterceptor securityFilter() {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setAccessDecisionManager(new AccessDecisionManager());
        filterSecurityInterceptor.setSecurityMetadataSource(new FilterInvocationSecurityMetadataSource());
        return filterSecurityInterceptor;
    }

    private CsrfFilter csrfFilter() {
        return new CsrfFilter();
    }
}
