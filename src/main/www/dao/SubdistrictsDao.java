package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.Subdistrict;

import java.util.List;
import java.util.Set;

/**
 * 办事处DAO接口
 *
 * @author 廿二月的天
 */
public interface SubdistrictsDao extends BaseDao<Subdistrict> {
    /**
     * 通过社区编号查询所属街道及社区
     *
     * @param communityId 社区编号
     * @return 所属街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Subdistrict selectSubdistrictAndCommunityByCommunityId(Long communityId) throws DataAccessException;

    /**
     * 通过街道编号查询所属街道及社区
     *
     * @param subdistrictId 社区编号
     * @return 所属街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Set<Subdistrict> selectSubdistrictAndCommunityBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 查询所有街道及社区
     *
     * @return 所有街道及社区
     * @throws DataAccessException 数据库操作异常
     */
    Set<Subdistrict> selectSubdistrictsAndCommunitiesAll() throws DataAccessException;

    /**
     * 统计所有街道社区居民
     *
     * @return 所有街道的社区居民的数量
     * @throws DataAccessException 数据库操作异常
     */
    List<Subdistrict> countCommunityResidents() throws DataAccessException;
}
