package main.service;

import java.util.Map;

import main.entity.CommunityResident;

/**
 * 社区居民业务接口
 *
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
	/**
	 * 通过社区居民编号查找社区居民与所属社区
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findCommunityResidentAndCommunityById(Integer id) throws Exception;
	/**
	 * 添加社区居民
	 * @param communityResident
	 * @param residentPhones
	 * @return
	 * @throws Exception
	 */
	public int createCommuntyResident(CommunityResident communityResident, String[] residentPhones) throws Exception;
	/**
	 * 更新社区居民
	 * @param communityResident
	 * @param residentPhones
	 * @throws Exception
	 */
	public int updateCommuntyResident(CommunityResident communityResident, String[] residentPhones) throws Exception;

	/**
	 * 通过社区居民查找匹配的社区居民
	 * @param communityResident
	 * @param pageNum
	 * @param unit
     * @param pageSize
	 * @return
	 * @throws Exception
	 */
    public Map<String,Object> findCommunityResidentByCommunityResident(CommunityResident communityResident, Integer pageNum, String unit, Integer pageSize) throws Exception;
}