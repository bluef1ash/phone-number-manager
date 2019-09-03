package com.github.phonenumbermanager.dao;

import com.github.phonenumbermanager.entity.CommunityResident;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.List;

/**
 * 社区居民DAO接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentDao extends BaseDao<CommunityResident> {

    /**
     * 通过社区居民查询社区居民与所属社区
     *
     * @param communityResident 社区居民
     * @return 多个社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 查询所有社区居民与所属社区和街道
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectAndCommunitiesAndSubdistrictByCommunityIds(@Param("communityIds") List<Serializable> communityIds) throws DataAccessException;

    /**
     * 通过社区居民查询社区居民与所属社区的数量
     *
     * @param communityResident 社区居民
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 通过多个社区编号查询所有社区居民与所属社区的数量
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countAndCommunityByCommunityIds(@Param("communityIds") List<Serializable> communityIds) throws DataAccessException;

    /**
     * 通过街道编号查询社区居民与所属社区的数量
     *
     * @param subdistrictId 街道编号
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countBySubdistrictId(Serializable subdistrictId) throws DataAccessException;
}
