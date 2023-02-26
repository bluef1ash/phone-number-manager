package com.github.phonenumbermanager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 测试类
 *
 * @author 廿二月的天
 */
@SpringBootTest
@Slf4j
@AllArgsConstructor
public class RedisTest {
    private final ConfigurationService configurationService;
    private final RedisUtil redisUtil;

    @Test
    public void onLoad() {
        List<Configuration> configurations = configurationService.list();
        Map<String, Object> configurationsMap =
            configurations.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
        redisUtil.hPutAll(SystemConstant.CONFIGURATIONS_MAP_KEY, configurationsMap);
        JSONObject jsonObject = JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY));
        Map<String, Configuration> map = jsonObject.toBean(Map.class);
        log.info(map.toString());
    }
}
