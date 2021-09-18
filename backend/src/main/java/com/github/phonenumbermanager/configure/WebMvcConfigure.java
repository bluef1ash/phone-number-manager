package com.github.phonenumbermanager.configure;

import javax.annotation.Resource;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC配置
 *
 * @author 廿二月的天
 */
@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {
    @Resource
    private EnumConvertFactory enumConvertFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumConvertFactory);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 错误页面配置
     *
     * @return 错误页面
     */
    @Bean
    public ErrorPageRegistrar containerCustomizer() {
        return (errorPageRegistry -> errorPageRegistry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"),
            new ErrorPage(HttpStatus.FORBIDDEN, "/exception")));
    }
}
