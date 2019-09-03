package com.github.phonenumbermanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用主入口
 *
 * @author 廿二月的天
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@MapperScan("com.github.phonenumbermanager.dao")
public class PhoneNumberManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneNumberManagerApplication.class, args);
    }
}
