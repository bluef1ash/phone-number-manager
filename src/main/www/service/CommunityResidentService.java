package www.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import www.entity.CommunityResident;
import www.entity.SystemUser;

import java.util.List;
import java.util.Map;


/**
 * 社区居民业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过社区居民编号查找社区居民与所属社区
     *
     * @param id 社区居民编号
     * @return 查找到的社区居民
     * @throws Exception SERVICE层异常
     */
    CommunityResident findCommunityResidentAndCommunityById(Integer id) throws Exception;

    /**
     * 添加社区居民
     *
     * @param communityResident 需要添加的社区居民
     * @throws Exception SERVICE层异常
     */
    void createCommunityResident(CommunityResident communityResident) throws Exception;

    /**
     * 更新社区居民
     *
     * @param communityResident 需要更新的社区居民
     * @return 更新数据库操作的行数
     * @throws Exception SERVICE层异常
     */
    void updateCommunityResident(CommunityResident communityResident) throws Exception;

    /**
     * 通过社区居民查找匹配的社区居民
     *
     * @param systemUser        登录的系统用户对象
     * @param configurationsMap 系统配置
     * @param communityResident 需要查找的社区居民
     * @param companyId         查找的范围单位的编号
     * @param companyRid        查找的范围单位的类别编号
     * @param pageNumber           分页页码
     * @param pageDataSize          每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, Map<String, Object> configurationsMap, CommunityResident communityResident, Long companyId, Long companyRid, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 从Excel导入数据
     *
     * @param workbook          Excel工作簿对象
     * @param subdistrictId     导入的街道编号
     * @param configurationsMap 系统配置
     * @return 导入的行数
     * @throws Exception SERVICE层异常
     */
    int addCommunityResidentFromExcel(Workbook workbook, Long subdistrictId, Map<String, Object> configurationsMap) throws Exception;

    /**
     * 查找所有社区居民及所属社区
     *
     * @param systemUser        登录的系统用户对象
     * @param configurationsMap 系统配置
     * @param pageNumber           分页页码
     * @param pageDataSize          每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Map<String, Object> configurationsMap, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过居民姓名与地址查找所属社区
     *
     * @param nameAddress         社区居民姓名与家庭住址拼接的字符串
     * @param communityResidentId 社区居民编号
     * @return 查找到的社区居民
     * @throws Exception SERVICE层异常
     */
    List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress, Long communityResidentId) throws Exception;

    /**
     * 通过居民联系方式与地址查找所属社区
     *
     * @param phones              社区居民的联系方式集合
     * @param communityResidentId 社区居民编号
     * @return 查找到的社区居民
     * @throws Exception SERVICE层异常
     */
    List<CommunityResident> findCommunityResidentByPhones(List<String> phones, Long communityResidentId) throws Exception;

    /**
     * 通过系统用户角色编号与定位角色编号查找社区居民及所属社区
     *
     * @param configurationsMap 系统配置
     * @param roleId            系统用户角色编号
     * @param roleLocationId    系统用户角色定位编号
     * @return 社区居民与所属社区集合转换的JSON对象
     * @throws Exception SERVICE层异常
     */
    JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, Long roleId, Long roleLocationId) throws Exception;

    /**
     * 查找Excel表头
     *
     * @return 社区居民表字段名称
     * @throws Exception SERVICE层异常
     */
    Map<String, String> getPartStatHead() throws Exception;

    /**
     * 计算并生成图表数据
     *
     * @param systemUser 系统用户对象
     * @return 图表需要的JSON对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> computedCount(SystemUser systemUser) throws Exception;
}
