package www.service;

import www.entity.Community;

import java.util.Map;

/**
 * 社区业务接口
 *
 */
public interface CommunityService extends BaseService<Community> {
	/**
	 * 通过社区ID查找社区和所属街道
	 * @param communityId
	 * @return
	 * @exception Exception
	 */
	public Community findCommunityAndSubdistrictById(Integer communityId) throws Exception;

	/**
	 * 查找所有社区和所属街道
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNum, Integer pageSize) throws Exception;
}
