package www.dao;

import www.entity.Community;

import java.util.List;

/**
 * 社区DAO接口
 */
public interface CommunitiesDao extends BaseDao<Community> {
    /**
     * 通过主键ID查询社区和所属街道
     *
     * @param communityId
     * @return
     * @throws Exception
     */
    public Community selectCommunityAndSubdistrictById(Integer communityId) throws Exception;

    /**
     * 查询所有社区和所属街道
     *
     * @return
     * @throws Exception
     */
    public List<Community> selectCommunitiesAndSubdistrictAll() throws Exception;

    /**
     * 通过所属街道编号查询社区
     *
     * @param subdistrictId
     * @return
     * @throws Exception
     */
    public List<Community> selectCommunitiesBySubdistrictId(Integer subdistrictId) throws Exception;

    /**
     * 通过社区名称查询社区编号
     *
     * @param communityAliasName
     * @return
     * @throws Exception
     */
    public Integer selectCommunityIdByCommunityName(String communityAliasName) throws Exception;

    /**
     * 通过所属街道名称查询社区
     *
     * @param subdistrictName
     * @return
     * @throws Exception
     */
    public List<Community> selectCommunitiesBySubdistrictName(String subdistrictName) throws Exception;

    /**
     * 通过社区编号查询指标
     *
     * @param communityId
     * @return
     * @throws Exception
     */
    public Integer selectActualNumberByCommunityId(Integer communityId) throws Exception;

    /**
     * 通过所属街道编号求和指标
     *
     * @param subdistrictId
     * @return
     * @throws Exception
     */
    public Integer sumActualNumberBySubdistrictId(Integer subdistrictId) throws Exception;

    /**
     * 求和所有社区指标
     *
     * @return
     * @throws Exception
     */
    public Integer sumActualNumber() throws Exception;

    /**
     * 通过所属街道编号统计各个社区
     *
     * @param subdistrictId
     * @return
     * @throws Exception
     */
    public List<Community> countCommunitiesBySubdistrictId(Integer subdistrictId) throws Exception;
}
