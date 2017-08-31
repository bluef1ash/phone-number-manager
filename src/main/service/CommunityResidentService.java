package main.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import main.entity.CommunityResident;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 社区居民业务接口
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过社区居民编号查找社区居民与所属社区
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Map<String, Object> findCommunityResidentAndCommunityById(Integer id) throws Exception;

    /**
     * 添加社区居民
     *
     * @param communityResident
     * @return
     * @throws Exception
     */
    public int createCommunityResident(CommunityResident communityResident) throws Exception;

    /**
     * 更新社区居民
     *
     * @param communityResident
     * @throws Exception
     */
    public int updateCommunityResident(CommunityResident communityResident) throws Exception;

    /**
     * 通过社区居民查找匹配的社区居民
     *
     * @param communityResident
     * @param pageNum
     * @param unit
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> findCommunityResidentByCommunityResident(CommunityResident communityResident, Integer pageNum, String unit, Integer pageSize) throws Exception;

    /**
     * 从Excel导入数据
     *
     * @param workbook
     * @return
     * @throws Exception
     */
    public int addCommunityResidentFromExcel(Workbook workbook) throws Exception;

    /**
     * 查找所有社区居民及所属社区
     *
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> findCommunityResidentsAndCommunity(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过居民姓名与地址查找所属社区
     * @param nameAddress
     * @return
     * @throws Exception
     */
    public List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress) throws Exception;

    /**
     * 通过居民联系方式与地址查找所属社区
     * @param phones
     * @throws Exception
     * @return
     */
    public List<CommunityResident> findCommunityResidentByPhones(Set<String> phones) throws Exception;
}
