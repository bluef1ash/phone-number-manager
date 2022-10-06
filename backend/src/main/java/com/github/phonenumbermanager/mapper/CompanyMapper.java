package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.entity.Company;

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
     * @return 全部查询单位信息的分页对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<Company> selectCorrelation() throws DataAccessException;
}
