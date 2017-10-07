package www.dao;

import www.entity.Subdistrict;

import java.util.List;
import java.util.Set;

/**
 * 办事处DAO接口
 */
public interface SubdistrictsDao extends BaseDao<Subdistrict> {
    /**
     * 通过社区编号查询所属街道及社区
     *
     * @param communityId
     * @return
     * @throws Exception
     */
    public Subdistrict selectSubdistrictAndCommunityByCommunityId(Integer communityId) throws Exception;

    /**
     * 通过街道编号查询所属街道及社区
     *
     * @param subdistrictId
     * @return
     * @throws Exception
     */
    public Set<Subdistrict> selectSubdistrictAndCommunityBySubdistrictId(Integer subdistrictId) throws Exception;

    /**
     * 查询所有街道及社区
     *
     * @return
     * @throws Exception
     */
    public Set<Subdistrict> selectSubdistrictsAndCommunitiesAll() throws Exception;

    /**
     * 统计所有街道社区居民
     *
     * @return
     * @throws Exception
     */
    public List<Subdistrict> countCommunityResidents() throws Exception;
}
