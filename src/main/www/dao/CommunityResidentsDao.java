package www.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import www.entity.CommunityResident;

import java.util.List;

/**
 * 社区居民DAO接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentsDao extends BaseDao<CommunityResident> {


    /**
     * 通过社区居民查询社区居民与所属社区
     *
     * @param communityResident 社区居民
     * @return 多个社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 查询所有社区居民与所属社区和街道
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(@Param("communityIds") List<Long> communityIds) throws DataAccessException;

    /**
     * 通过社区居民查询社区居民与所属社区的数量
     *
     * @param communityResident 社区居民
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countCommunityResidentsByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 通过多个社区编号查询所有社区居民与所属社区的数量
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countCommunityResidentsAndCommunityByCommunityIds(@Param("communityIds") List<Long> communityIds) throws DataAccessException;

    /**
     * 通过街道编号查询社区居民与所属社区的数量
     *
     * @param subdistrictId 街道编号
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countCommunityResidentsBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 统计所有社区居民数量
     *
     * @return 所有社区居民数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countCommunityResidents() throws DataAccessException;
}
