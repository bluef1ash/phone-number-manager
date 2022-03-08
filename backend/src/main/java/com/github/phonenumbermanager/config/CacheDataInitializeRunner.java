package com.github.phonenumbermanager.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * 缓存数据初始化
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Component
public class CacheDataInitializeRunner implements CommandLineRunner {
    private final ConfigurationService configurationService;
    private final SystemPermissionService systemPermissionService;
    private final RedisUtil redisUtil;

    @Override
    public void run(String... args) {
        List<Configuration> configurationList = configurationService.list();
        Map<String, Configuration> configurationM =
            configurationList.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
        String jsonStr = JSONUtil.toJsonStr(configurationM);
        redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, jsonStr);
        redisUtil.set(SystemConstant.SYSTEM_PERMISSIONS_KEY, systemPermissionService.listCorrelation());
    }
}
