package com.github.phonenumbermanager.config;

import com.github.phonenumbermanager.security.FilterInvocationSecurityMetadataSource;
import com.github.phonenumbermanager.security.filter.CaptchaValidFilter;
import com.github.phonenumbermanager.security.filter.JwtTokenFilter;
import com.github.phonenumbermanager.security.manager.AccessDecisionManager;
import com.github.phonenumbermanager.service.SystemUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Spring Security配置
 *
 * @author 廿二月的天
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String[] PERMIT_WHITELIST = {"/swagger-resources/**", "/swagger-ui.html", "/v3/api-docs", "/webjars/**", "/swagger-ui/**", "/druid/**"};
    private static final String[] ANONYMOUS_WHITELIST = {"/account/login", "/account/getRecaptcha", "/loginError"};
    @Resource
    private SystemUserService systemUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().headers().frameOptions().sameOrigin().cacheControl();
        http.logout().logoutUrl("/account/logout").and().exceptionHandling().and().addFilterBefore(captchaValidFilter(), UsernamePasswordAuthenticationFilter.class).addFilterAfter(authenticationTokenFilter(), CaptchaValidFilter.class);
        http.authorizeRequests().antMatchers(PERMIT_WHITELIST).permitAll().antMatchers(ANONYMOUS_WHITELIST).anonymous().anyRequest().access("@systemUserService.hasPermission(request, authentication)");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(PERMIT_WHITELIST);
    }

    @Bean
    AuthenticationProvider authProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(systemUserService);
        return daoAuthenticationProvider;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    JwtTokenFilter authenticationTokenFilter() {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter();
        jwtTokenFilter.setSystemUserService(systemUserService);
        return jwtTokenFilter;
    }

    CaptchaValidFilter captchaValidFilter() {
        return new CaptchaValidFilter();
    }

    FilterSecurityInterceptor securityFilter() {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setAccessDecisionManager(new AccessDecisionManager());
        filterSecurityInterceptor.setSecurityMetadataSource(new FilterInvocationSecurityMetadataSource());
        return filterSecurityInterceptor;
    }
}
