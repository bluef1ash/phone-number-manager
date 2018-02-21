package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.Configuration;

/**
 * 系统配置DAO接口
 *
 * @author 廿二月的天
 */
public interface ConfigurationsDao extends BaseDao<Configuration> {
    /**
     * 通过系统配置项关键字名称查询
     *
     * @param key 系统配置项关键字名称
     * @return 系统配置项对象
     * @throws DataAccessException 数据库操作异常
     */
    Configuration selectConfigurationByKey(String key) throws DataAccessException;

    /**
     * 通过系统配置项关键字名称删除
     *
     * @param key 系统配置项关键字名称
     * @throws DataAccessException 数据库操作异常
     */
    void deleteConfigurationByKey(String key) throws DataAccessException;
}
