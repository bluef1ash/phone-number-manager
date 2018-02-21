package www.service.impl;

import org.springframework.stereotype.Service;
import www.entity.Configuration;
import www.service.ConfigurationService;

/**
 * 系统配置Service层实现
 *
 * @author 廿二月的天
 */
@Service("configurationService")
public class ConfigurationServiceImpl extends BaseServiceImpl<Configuration> implements ConfigurationService {
    @Override
    public Configuration findConfigurationByKey(String key) throws Exception {
        return configurationsDao.selectConfigurationByKey(key);
    }

    @Override
    public boolean deleteConfigurationByKey(String key) throws Exception {
        Configuration configuration = configurationsDao.selectConfigurationByKey(key);
        if (configuration.getKeyIsChanged() != 0) {
            configurationsDao.deleteConfigurationByKey(key);
            return true;
        }
        return false;
    }
}
