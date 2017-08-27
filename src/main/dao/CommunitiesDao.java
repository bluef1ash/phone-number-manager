package main.dao;

import main.entity.Community;

import java.util.List;

/**
 * 社区DAO接口
 *
 */
public interface CommunitiesDao extends BaseDao<Community> {
	/**
	 * 通过主键ID查询社区和所属街道
	 * @param communityId
	 * @return
	 * @throws Exception
	 */
	public Community selectCommunityAndSubdistrictById(Integer communityId) throws Exception;

	/**
	 * 查询所有社区和所属街道
	 * @return
	 * @throws Exception
	 */
	public List<Community> selectCommunitiesAndSubdistrictAll() throws Exception;

	/**
	 * 通过所属街道编号查询社区
	 * @param subdistrictId
	 * @return
	 * @throws Exception
	 */
	public List<Community> selectCommunitiesBySubdistrictId(Integer subdistrictId) throws Exception;
}