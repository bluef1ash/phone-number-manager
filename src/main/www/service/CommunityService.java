package www.service;

import www.entity.Community;
import www.entity.CommunityResident;
import www.entity.SystemUser;

import java.util.List;
import java.util.Map;

/**
 * 社区业务接口
 */
public interface CommunityService extends BaseService<Community> {
    /**
     * 通过社区ID查找社区和所属街道
     *
     * @param communityId
     * @return
     * @throws Exception
     */
    public Community findCommunityAndSubdistrictById(Integer communityId) throws Exception;

    /**
     * 查找所有社区和所属街道
     *
     * @return
     * @throws Exception
     */
    public Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过社区联系方式查找
     *
     * @param community
     * @return
     * @throws Exception
     */
    public List<Community> findCommunityByCommunity(Community community) throws Exception;

    /**
     * 通过系统用户查找
     *
     * @param systemUser
     * @return
     * @throws Exception
     */
    public List<Community> findCommunitiesBySystemUser(SystemUser systemUser) throws Exception;
}
