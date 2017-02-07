package la.isx.phone_number_manager.main.dao;

import la.isx.phone_number_manager.main.entity.Community;
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
}