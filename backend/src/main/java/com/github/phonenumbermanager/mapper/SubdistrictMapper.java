package com.github.phonenumbermanager.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Subdistrict;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 办事处Mapper接口
 *
 * @author 廿二月的天
 */
@Repository
public interface SubdistrictMapper extends CommonMapper<Subdistrict> {
    /**
     * 通过社区编号查询所属街道及社区
     *
     * @param communityId 社区编号
     * @return 所属街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Subdistrict selectOneAndCommunityByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号查询所属街道及社区
     *
     * @param id 社区编号
     * @return 所属街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Set<Subdistrict> selectCorrelationAndCommunityById(Serializable id) throws DataAccessException;

    /**
     * 查询所有街道及社区
     *
     * @return 所有街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Set<Subdistrict> selectAndCommunities() throws DataAccessException;

    /**
     * 统计所有街道社区居民
     *
     * @return 所有街道的社区居民的数量
     * @throws DataAccessException 数据库操作异常
     */
    @MapKey("key")
    Map<String, Long> countCommunityResidents() throws DataAccessException;

    /**
     * 查询所有街道及所属社区
     *
     * @return 所有街道及所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<Subdistrict> selectAndPhoneNumbersAll() throws DataAccessException;

    /**
     * 查询所有街道及所属社区
     *
     * @return 所有街道及所属社区
     * @throws DataAccessException 数据库操作异常
     */
    IPage<Subdistrict> selectAndPhoneNumbersAll(Page<Subdistrict> page) throws DataAccessException;
}
