package www.service.impl;

import com.github.pagehelper.PageHelper;
import constant.SystemConstant;
import org.springframework.stereotype.Service;

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
    public Community findCommunityAndSubdistrictById(Integer communityId) throws Exception {
        return communitiesDao.selectCommunityAndSubdistrictById(communityId);
    }

    @Override
    public Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<Community> data = communitiesDao.selectCommunitiesAndSubdistrictAll();
        return findObjectsMethod(data);
    }

    @Override
    public List<Community> findCommunityByCommunity(Community community) throws Exception {
        return baseDao.selectObjectsByObject(community);
    }

    @Override
    public List<Community> findCommunitiesBySystemUser(SystemUser systemUser) throws Exception {
        Integer roleId = systemUser.getRoleId();
        List<Community> communities;
        switch (roleId) {
            case SystemConstant.COMMUNITY_ROLE_ID:
                communities = new ArrayList<>();
                Community community = communitiesDao.selectObjectById(systemUser.getRoleLocationId());
                communities.add(community);
                break;
            case SystemConstant.SUBDISTRICT_ROLE_ID:
                communities = communitiesDao.selectCommunitiesBySubdistrictId(systemUser.getRoleLocationId());
                break;
            default:
                communities = communitiesDao.selectObjectsAll();
                break;
        }
        return communities;
    }
}
