package main.dao;

import main.entity.CommunityResident;

import java.util.List;
import java.util.Set;

/**
 * 社区居民DAO接口
 */
public interface CommunityResidentsDao extends BaseDao<CommunityResident> {
    /**
     * 通过社区居民主键ID查询社区居民与所属社区
     *
     * @param id
     * @return
     * @throws Exception
     */
    public CommunityResident selectCommunityResidentAndCommunityById(Integer id) throws Exception;

    /**
     * 通过社区居民查询社区居民与所属社区
     *
     * @param communityResident
     * @return
     * @throws Exception
     */
    public List<CommunityResident> selectCommunityResidentsByCommunityResidentAndCommunityIds(CommunityResident communityResident) throws Exception;

    /**
     * 批量插入社区居民数据
     *
     * @param residents
     * @return
     * @throws Exception
     */
    public int insertBatchCommunityResidents(List<CommunityResident> residents) throws Exception;

    /**
     * 查询所有社区居民与所属社区
     * @return
     * @throws Exception
     */
    public List<CommunityResident> selectCommunityResidentsAndCommunity() throws Exception;

    /**
     * 通过姓名+地址查询姓名与社区编号
     * @param nameAddress
     * @return
     * @throws Exception
     */
    public List<CommunityResident> selectCommunityResidentByNameAndAddress(String nameAddress) throws Exception;

    /**
     * 通过电话数组查询姓名与社区编号
     * @param residentPhones
     * @return
     * @throws Exception
     */
    public List<CommunityResident> selectCommunityResidentByPhones(Set<String> residentPhones) throws Exception;
}
