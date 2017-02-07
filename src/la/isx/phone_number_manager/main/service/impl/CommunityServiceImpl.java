package la.isx.phone_number_manager.main.service.impl;

import org.springframework.stereotype.Service;

import la.isx.phone_number_manager.main.entity.Community;
import la.isx.phone_number_manager.main.service.CommunityService;

/**
 * 社区业务实现
 *
 */
@Service("communityService")
public class CommunityServiceImpl extends BaseServiceImpl<Community> implements CommunityService {
	@Override
	public Community findCommunityAndSubdistrictById(Integer communityId) throws Exception {
		return communitiesDao.selectCommunityAndSubdistrictById(communityId);
	}
}