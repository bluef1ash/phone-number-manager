package www.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import utils.ExcelUtil;
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
    CommunityResident findCommunityResidentAndCommunityById(Long id) throws Exception;

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
     * @throws Exception SERVICE层异常
     */
    void updateCommunityResident(CommunityResident communityResident) throws Exception;

    /**
     * 通过社区居民查找匹配的社区居民
     *
     * @param systemUser        登录的系统用户对象
     * @param systemRoleId      系统角色编号
     * @param communityRoleId   社区角色编号
     * @param subdistrictRoleId 街道角色编号
     * @param communityResident 需要查找的社区居民
     * @param companyId         查找的范围单位的编号
     * @param companyRoleId     查找的范围单位的类别编号
     * @param pageNumber        分页页码
     * @param pageDataSize      每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, CommunityResident communityResident, Long companyId, Long companyRoleId, Integer pageNumber, Integer pageDataSize) throws Exception;

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
     * @param systemRoleId      系统角色编号
     * @param communityRoleId   社区角色编号
     * @param subdistrictRoleId 街道角色编号
     * @param pageNumber        分页页码
     * @param pageDataSize      每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过系统用户角色编号与定位角色编号查找社区居民及所属社区
     *
     * @param configurationsMap 系统配置
     * @param userData          用户数据
     * @return 社区居民与所属社区集合转换的JSON对象
     * @throws Exception SERVICE层异常
     */
    JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, List<Map<String, Object>> userData) throws Exception;

    /**
     * 查找Excel表头
     *
     * @return 社区居民表字段名称
     */
    Map<String, String> getPartStatHead();


    /**
     * 设置Excel头部
     *
     * @param titles 标题数组
     * @return 设置接口
     * @throws Exception SERVICE层异常
     */
    ExcelUtil.DataHandler setExcelHead(String[] titles) throws Exception;

    /**
     * 获取社区居民录入统计信息
     *
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 统计信息对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> getBaseMessage(Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) throws Exception;

    /**
     * 获取社区居民柱状图数据
     *
     * @param systemUser        正在登录中的系统用户对象
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 柱状图数据
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> getChartBar(SystemUser systemUser, Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) throws Exception;
}
