package www.service;

import www.entity.Community;
import www.entity.SystemUser;

import java.util.List;
import java.util.Map;

/**
 * 社区业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityService extends BaseService<Community> {
    /**
     * 通过社区ID查找社区和所属街道
     *
     * @param communityId 社区编号
     * @return 查找到的社区和所属街道
     * @throws Exception SERVICE层异常
     */
    Community findCommunityAndSubdistrictById(Integer communityId) throws Exception;

    /**
     * 查找所有社区和所属街道
     *
     * @param pageNum  分页页码
     * @param pageSize 每页显示条目数量
     * @return 查找道德所有社区和所属街道与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCommunitiesAndSubdistrict(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过社区联系方式查找
     *
     * @param community 需要查找的社区信息对象
     * @return 查找到的社区对象
     * @throws Exception SERVICE层异常
     */
    List<Community> findCommunityByCommunity(Community community) throws Exception;

    /**
     * 通过系统用户查找
     *
     * @param systemUser        系统用户对象
     * @param configurationsMap 系统配置
     * @return 查找到的社区对象的集合
     * @throws Exception SERVICE层异常
     */
    List<Community> findCommunitiesBySystemUser(SystemUser systemUser, Map<String, Object> configurationsMap) throws Exception;
}
