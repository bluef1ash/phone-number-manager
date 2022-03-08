package com.github.phonenumbermanager;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class RedisTest {
    private ConfigurationService configurationService;
    private RedisUtil redisUtil;

    @Test
    public void onLoad() {
        // List<Configuration> configurations = configurationService.list();
        // Map<String, Configuration> configurationsMap =
        // configurations.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
        // redisTemplate.opsForHash().putAll(SystemConstant.CONFIGURATIONS_MAP_KEY, configurationsMap);
        JSONObject jsonObject = JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY));
        Map<String, Configuration> map = jsonObject.toBean(Map.class);
        log.info(map.toString());
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
