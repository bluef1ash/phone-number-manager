package www.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import www.entity.Community;
import www.entity.SystemUser;
import www.service.CommunityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 社区业务实现
 *
 * @author 廿二月的天
 */
@Service("communityService")
public class CommunityServiceImpl extends BaseServiceImpl<Community> implements CommunityService {
    @Override
    public Community findCommunityAndSubdistrictById(Long communityId) throws Exception {
        return communitiesDao.selectCommunityAndSubdistrictById(communityId);
    }

    @Override
    public Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNumber, Integer pageDataSize) throws Exception {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        PageHelper.startPage(pageNumber, pageDataSize);
        List<Community> data = communitiesDao.selectCommunitiesAndSubdistrictAll();
        return findObjectsMethod(data);
    }

    @Override
    public List<Community> findCommunityByCommunity(Community community) throws Exception {
        return baseDao.selectObjectsByObject(community);
    }

    @Override
    public List<Community> findCommunitiesBySystemUser(SystemUser systemUser, Map<String, Object> configurationsMap) throws Exception {
        List<Community> communities;
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        if (systemUser.getRoleId().equals(communityRoleId)) {
            communities = new ArrayList<>();
            Community community = communitiesDao.selectCommunityCorrelationById(systemUser.getRoleLocationId());
            communities.add(community);
        } else if (systemUser.getRoleId().equals(subdistrictRoleId)) {
            communities = communitiesDao.selectCommunitiesBySubdistrictId(systemUser.getRoleLocationId());
        } else {
            communities = communitiesDao.selectCommunitiesCorrelationSubdistrictsAll();
        }
        return communities;
    }
}
