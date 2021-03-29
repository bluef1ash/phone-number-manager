package com.github.phonenumbermanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 应用主入口
 *
 * @author 廿二月的天
 */

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.github.phonenumbermanager.mapper")
public class PhoneNumberManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneNumberManagerApplication.class, args);
    }
}
