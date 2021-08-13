package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Subcontractor;

/**
 * 社区分包人Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface SubcontractorMapper extends CommonMapper<Subcontractor> {
    /**
     * 通过所属社区编号编号查询
     *
     * @param communityId
     *            所属社区编号
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    List<Subcontractor> select(Serializable communityId) throws DataAccessException;

    /**
     * 通过所属社区编号编号查询
     *
     * @param page
     *            分页对象
     * @param communityId
     *            所属社区编号
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    IPage<Subcontractor> select(Page<Subcontractor> page, Serializable communityId) throws DataAccessException;

    /**
     * 通过所属街道编号查询
     *
     * @param subdistrictId
     *            所属街道编号
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    List<Subcontractor> selectBySubdistrictId(Serializable subdistrictId) throws DataAccessException;

    /**
     * 通过所属街道编号分页查询
     *
     * @param page
     *            分页对象
     * @param subdistrictId
     *            所属街道编号
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    IPage<Subcontractor> selectBySubdistrictId(Page<Subcontractor> page, Serializable subdistrictId)
        throws DataAccessException;

    /**
     * 关联查询所有
     *
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    List<Subcontractor> selectCorrelationAll() throws DataAccessException;

    /**
     * 关联查询分页所有
     *
     * @param page
     *            分页对象
     * @return 社区分包人对象集合
     * @throws DataAccessException
     *             数据库连接异常
     */
    IPage<Subcontractor> selectCorrelationAll(Page<Subcontractor> page) throws DataAccessException;

    /**
     * 通过编号查询
     *
     * @param id
     *            需要查询的对象编号
     * @return 查询到的对象
     * @throws DataAccessException
     *             数据库连接异常
     */
    Subcontractor getCorrelationById(Serializable id) throws DataAccessException;
}
