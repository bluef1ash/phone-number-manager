package com.github.phonenumbermanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * 系统运行时初始化
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Component
public class InitializeRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        cacheInitialize();
    }

    /**
     * 初始化缓存数据
     */
    private void cacheInitialize() {}
}
