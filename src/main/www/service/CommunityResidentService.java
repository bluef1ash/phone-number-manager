package www.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import www.entity.CommunityResident;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpSession;

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
    public CommunityResident findCommunityResidentAndCommunityById(Integer id) throws Exception;

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
     * @param company
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> findCommunityResidentByCommunityResident(CommunityResident communityResident, String company, Integer pageNum, Integer pageSize) throws Exception;

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
     *
     * @param nameAddress
     * @param communityResidentId
     * @return
     * @throws Exception
     */
    public List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress, Integer communityResidentId) throws Exception;

    /**
     * 通过居民联系方式与地址查找所属社区
     *
     * @param communityResidentId
     * @param phones
     * @return
     * @throws Exception
     */
    public List<CommunityResident> findCommunityResidentByPhones(Integer communityResidentId, List<String> phones) throws Exception;

    /**
     * 通过系统用户角色编号与定位角色编号查找社区居民及所属社区
     *
     * @param roleId
     * @param roleLocationId
     * @return
     * @throws Exception
     */
    public JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Integer roleId, Integer roleLocationId) throws Exception;

    /**
     * 查找Excel表头
     *
     * @return
     * @throws Exception
     */
    public Map<String, String> getPartStatHead() throws Exception;

    /**
     * 计算并生成图表数据
     *
     * @param session
     * @return
     * @throws Exception
     */
    public Map<String, Object> computedCount(HttpSession session) throws Exception;
}
