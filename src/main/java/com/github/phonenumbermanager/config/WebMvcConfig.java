package com.github.phonenumbermanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Spring MVC配置
 *
 * @author 廿二月的天
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private EnumConvertFactory enumConvertFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumConvertFactory);
    }
}
