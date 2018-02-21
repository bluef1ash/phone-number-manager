package www.service;

import www.entity.Configuration;

/**
 * 系统配置Service层
 *
 * @author 廿二月的天
 */
public interface ConfigurationService extends BaseService<Configuration> {
    /**
     * 通过系统配置项关键字名称查找
     *
     * @param key 系统配置项关键字名称
     * @return 系统配置项
     * @throws Exception DAO异常
     */
    Configuration findConfigurationByKey(String key) throws Exception;

    /**
     * 通过系统配置项关键字名称删除
     * @param key 系统配置项关键字名称
     * @throws Exception DAO异常
     */
    boolean deleteConfigurationByKey(String key) throws Exception;
}
