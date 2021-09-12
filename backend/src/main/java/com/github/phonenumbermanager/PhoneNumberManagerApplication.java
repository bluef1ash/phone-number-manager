package com.github.phonenumbermanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * 应用主入口
 *
 * @author 廿二月的天
 */

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableOpenApi
@MapperScan("com.github.phonenumbermanager.mapper")
public class PhoneNumberManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneNumberManagerApplication.class, args);
    }
}
