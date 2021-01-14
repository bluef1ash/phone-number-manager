package com.github.phonenumbermanager.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 应用配置
 *
 * @author 廿二月的天
 */
@Configuration
public class ApplicationConfig {
    @Bean
    public ErrorPageRegistrar containerCustomizer() {
        return (errorPageRegistry -> errorPageRegistry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/exception"), new ErrorPage(HttpStatus.FORBIDDEN, "/exception")));
    }
}
