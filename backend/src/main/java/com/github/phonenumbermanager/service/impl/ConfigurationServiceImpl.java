package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.ConfigurationMapper;
import com.github.phonenumbermanager.service.ConfigurationService;

/**
 * 系统配置业务实现
 *
 * @author 廿二月的天
 */
@Service("configurationService")
public class ConfigurationServiceImpl extends BaseServiceImpl<ConfigurationMapper, Configuration>
    implements ConfigurationService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable key) {
        Configuration configuration = baseMapper.selectById(key);
        if (configuration.getKeyIsChanged()) {
            baseMapper.deleteById(key);
            return true;
        }
        throw new BusinessException("不允许删除内置系统配置");
    }
}
