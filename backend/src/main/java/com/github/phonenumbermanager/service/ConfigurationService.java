package com.github.phonenumbermanager.service;

import java.util.Map;

import com.github.phonenumbermanager.entity.Configuration;

import cn.hutool.json.JSONObject;

/**
 * 系统配置业务接口
 *
 * @author 廿二月的天
 */
public interface ConfigurationService extends BaseService<Configuration> {
    /**
     * 获取所有系统配置
     *
     * @return 所有系统配置 Map 集合
     */
    Map<String, JSONObject> mapAll();
}
