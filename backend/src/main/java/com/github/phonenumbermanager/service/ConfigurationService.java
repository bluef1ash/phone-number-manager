package com.github.phonenumbermanager.service;

import java.util.Map;

import com.github.phonenumbermanager.entity.Configuration;

/**
 * 系统配置Service层
 *
 * @author 廿二月的天
 */
public interface ConfigurationService extends BaseService<Configuration> {
    /**
     * 获取所有系统配置
     *
     * @return 所有系统配置Map
     */
    Map<String, Configuration> mapAll();
}
