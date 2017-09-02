package main.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import main.entity.Community;
import main.service.CommunityService;

import java.util.List;
import java.util.Map;

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

	@Override
	public Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
		List<Community> data = communitiesDao.selectCommunitiesAndSubdistrictAll();
		return findObjectsMethod(data, pageNum, pageSize);
	}
}
