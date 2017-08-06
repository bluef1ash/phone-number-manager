package main.service.impl;

import org.springframework.stereotype.Service;

import main.entity.Community;
import main.service.CommunityService;

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