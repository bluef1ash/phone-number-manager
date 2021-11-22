package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

/**
 * 基础Mapper接口
 *
 * @param <T>
 *            实体对象类
 * @author 廿二月的天
 */
@Mapper
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    /**
     * 通过单位查询
     *
     * @param companyIds
     *            单位上级编号
     * @return 社区楼长集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectListByCompanyIds(List<Long> companyIds) throws DataAccessException;

    /**
     * 通过编号查询对象与所属社区
     *
     * @param id
     *            对象编号
     * @return 对象与所属社区
     * @throws DataAccessException
     *             数据库操作异常
     */
    T selectAndCompanyById(Serializable id) throws DataAccessException;

    /**
     * 单位分组统计数量
     *
     * @param companyIds
     *            单位编号集合
     * @return 社区数量对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    @MapKey("name")
    List<Map<String, Object>> countForGroupCompany(List<Long> companyIds) throws DataAccessException;
}
