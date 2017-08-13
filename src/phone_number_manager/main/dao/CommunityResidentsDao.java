package main.dao;

import main.entity.CommunityResident;

import java.util.List;
import java.util.Set;

/**
 * 社区居民DAO接口
 *
 */
public interface CommunityResidentsDao extends BaseDao<CommunityResident> {
	/**
     * 通过社区居民主键ID查询社区居民与所属社区
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CommunityResident selectCommunityResidentAndCommunityById(Integer id) throws Exception;

    /**
     * 通过社区居民查询社区居民与所属社区
     * @param communityResident
     * @return
     * @throws Exception
     */
    public List<CommunityResident> selectCommunityResidentsByCommunityResidentAndCommunityIds(CommunityResident communityResident);
}