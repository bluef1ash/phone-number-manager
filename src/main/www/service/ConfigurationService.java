package www.service;

import www.entity.Configuration;

/**
 * 系统配置Service层
 *
 * @author 廿二月的天
 */
public interface ConfigurationService extends BaseService<Configuration> {

    /**
     * 通过系统配置项关键字名称删除
     *
     * @param key 系统配置项关键字名称
     * @return 是否删除成功
     * @throws Exception DAO异常
     */
    boolean deleteConfigurationByKey(String key) throws Exception;
}
