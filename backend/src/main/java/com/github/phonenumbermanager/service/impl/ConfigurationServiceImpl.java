package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.mapper.ConfigurationMapper;
import com.github.phonenumbermanager.service.ConfigurationService;

/**
 * 系统配置业务实现
 *
 * @author 廿二月的天
 */
@Service
public class ConfigurationServiceImpl extends BaseServiceImpl<ConfigurationMapper, Configuration>
    implements ConfigurationService {}
