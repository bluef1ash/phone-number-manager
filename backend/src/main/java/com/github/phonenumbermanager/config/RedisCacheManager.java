package com.github.phonenumbermanager.config;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import cn.hutool.core.math.Calculator;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 自定义 Redis 管理配置
 *
 * @author 廿二月的天
 */
public class RedisCacheManager extends org.springframework.data.redis.cache.RedisCacheManager {
    private final Pattern pattern = Pattern.compile("(?iUs)^(.*?)(?:\\$\\{(.*)})?$");

    public RedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @NotNull
    @Override
    protected RedisCache createRedisCache(@NotNull String name, RedisCacheConfiguration cacheConfig) {
        String jsonString = null;
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            name = matcher.group(1);
            jsonString = matcher.group(2);
        }
        if (jsonString != null) {
            JSONObject entries = JSONUtil.parseObj(jsonString);
            if (!entries.isEmpty()) {
                if (!entries.isNull("ttl")) {
                    long ttl = (long)Calculator.conversion(entries.get("ttl").toString());
                    cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
                }
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }

}
