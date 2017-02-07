package la.isx.phone_number_manager.main.service;

import la.isx.phone_number_manager.main.entity.Community;
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
}