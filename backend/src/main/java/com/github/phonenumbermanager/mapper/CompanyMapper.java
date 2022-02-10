package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Company;

import cn.hutool.json.JSONObject;

/**
 * 单位Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
    /**
     * 通过主键关联查询
     *
     * @param id
     *            单位编号
     * @return 一个单位信息对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    Company selectCorrelationById(Serializable id) throws DataAccessException;

    /**
     * 查询所有单位
     *
     * @param companies
     *            需要查询的单位集合
     * @return 全部查询的社区和所属街道的信息
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<Company> selectCorrelationByCompanies(List<Company> companies) throws DataAccessException;

    /**
     * 查询所有单位
     *
     * @param companies
     *            需要查询的单位集合
     * @param page
     *            分页对象
     * @param search
     *            搜索条件
     * @param sort
     *            排序条件
     * @return 全部查询的社区和所属街道的信息
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<Company> selectCorrelationByCompanies(List<Company> companies, Page<Company> page, JSONObject search,
        JSONObject sort) throws DataAccessException;

    /**
     * 查询所有单位
     *
     * @return 全部查询单位信息的分页对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<Company> selectCorrelation() throws DataAccessException;

    /**
     * 查询所有单位（分页）
     *
     * @param page
     *            分页对象
     * @return 全部查询单位信息的分页对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<Company> selectCorrelation(Page<Company> page) throws DataAccessException;
}
