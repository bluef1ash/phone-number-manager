package com.github.phonenumbermanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * 应用主入口
 *
 * @author 廿二月的天
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableTransactionManagement
@EnableOpenApi
@MapperScan("com.github.phonenumbermanager.mapper")
@EnableCaching(order = Ordered.HIGHEST_PRECEDENCE)
@EnableAsync
public class PhoneNumberManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneNumberManagerApplication.class, args);
    }
}
