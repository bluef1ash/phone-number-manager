package com.github.phonenumbermanager;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
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
    @Resource
    private ConfigurationService configurationService;
    @Resource
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
}
