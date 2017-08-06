package main.dao;

import main.entity.CommunityResident;
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
}