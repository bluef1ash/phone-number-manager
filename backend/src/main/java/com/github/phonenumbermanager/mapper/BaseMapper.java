package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Company;

import cn.hutool.json.JSONObject;

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
     * 批量插入数据
     *
     * @param list
     *            数据对象
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int insertBatchSomeColumn(Collection<T> list);

    /**
     * 插入数据忽略重复
     *
     * @param entity
     *            数据对象
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int insertIgnore(T entity);

    /**
     * 批量插入数据忽略重复
     *
     * @param list
     *            数据对象
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int insertIgnoreBatchSomeColumn(Collection<T> list);

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
    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> selectCountForGroupCompany(List<Long> companyIds) throws DataAccessException;

    /**
     * 查询所有对象（分页）
     *
     * @param page
     *            分页对象
     * @param companies
     *            需要查询的单位集合
     * @param search
     *            搜索条件
     * @param sort
     *            排序条件
     * @return 全部查询的数据和所属街道的信息
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<T> selectCorrelationByCompanies(Page<T> page, List<Company> companies, JSONObject search, JSONObject sort)
        throws DataAccessException;

    /**
     * 查询计数所有对象（分页）
     *
     * @param companies
     *            需要查询的单位集合
     * @param search
     *            搜索条件
     * @param sort
     *            排序条件
     * @return 全部查询的数据和所属街道的信息数量
     * @throws DataAccessException
     *             数据库操作异常
     */
    long selectCorrelationCountByCompanies(List<Company> companies, JSONObject search, JSONObject sort)
        throws DataAccessException;

    /**
     * 单位统计子单位数量
     *
     * @param companyIdMap
     *            单位编号集合 Map
     * @return 社区数量对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> selectCountForSubQueryCompany(@Param("companyIdMap") Map<Long, List<Long>> companyIdMap)
        throws DataAccessException;
}
