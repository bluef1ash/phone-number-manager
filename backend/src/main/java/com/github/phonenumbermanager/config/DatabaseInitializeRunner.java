package com.github.phonenumbermanager.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.service.ConfigurationService;

/**
 * 数据库数据初始化
 *
 * @author 廿二月的天
 */
@Component
public class DatabaseInitializeRunner implements CommandLineRunner {
    @Resource
    private ConfigurationService configurationService;

    @Override
    public void run(String... args) {
        if (configurationService.list() != null) {
            List<Configuration> configurations = new ArrayList<>();

            configurationService.saveBatch(configurations);
        }
    }
}
