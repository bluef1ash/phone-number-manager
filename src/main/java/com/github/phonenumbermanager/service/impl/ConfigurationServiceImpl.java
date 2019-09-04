package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 系统配置Service层实现
 *
 * @author 廿二月的天
 */
@Service("configurationService")
public class ConfigurationServiceImpl extends BaseServiceImpl<Configuration> implements ConfigurationService {

    @Override
    public long update(Configuration configuration) {
        configuration.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(configuration);
    }

    @Override
    public long delete(String key) {
        Configuration configuration = configurationDao.selectById(key);
        if (configuration.getKeyChanged()) {
            return configurationDao.deleteById(key);
        }
        throw new BusinessException("不允许删除内置系统配置");
    }
}
