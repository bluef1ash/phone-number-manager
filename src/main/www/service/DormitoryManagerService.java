package www.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import www.entity.DormitoryManager;
import www.entity.SystemUser;

import java.util.List;
import java.util.Map;

/**
 * 社区楼长Service接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagerService extends BaseService<DormitoryManager> {
    /**
     * 查找所有社区楼长及所属社区
     *
     * @param systemUser        登录的系统用户对象
     * @param systemRoleId      系统角色编号
     * @param communityRoleId   社区角色编号
     * @param subdistrictRoleId 街道角色编号
     * @param pageNumber        分页页码
     * @param pageDataSize      每页展示的数量
     * @return 查找到的社区楼长集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findDormitoryManagersAndCommunity(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过楼长编号查找楼长与所属社区
     *
     * @param id 楼长编号
     * @return 楼长对象
     * @throws Exception SERVICE层异常
     */
    DormitoryManager findDormitoryManagerAndCommunityById(String id) throws Exception;

    /**
     * 保存社区楼长
     *
     * @param dormitoryManager 需要保存的社区楼长
     * @throws Exception SERVICE层异常
     */
    void createDormitoryManager(DormitoryManager dormitoryManager) throws Exception;

    /**
     * 更新社区楼长
     *
     * @param dormitoryManager 需要更新的社区楼长
     * @throws Exception SERVICE层异常
     */
    void updateDormitoryManager(DormitoryManager dormitoryManager) throws Exception;

    /**
     * 从Excel导入数据
     *
     * @param workbook          Excel工作簿对象
     * @param subdistrictId     导入的街道编号
     * @param configurationsMap 系统配置
     * @return 导入的行数
     * @throws Exception SERVICE层异常
     */
    int addDormitoryManagerFromExcel(Workbook workbook, Long subdistrictId, Map<String, Object> configurationsMap) throws Exception;

    /**
     * 查找Excel表头
     *
     * @return 社区楼长表字段名称
     */
    Map<String, String> getPartStatHead();

    /**
     * 通过系统用户角色编号与定位角色编号查找社区楼长及所属社区
     *
     * @param configurationsMap 系统配置
     * @param userData          用户数据
     * @param titles            标题数组
     * @return 社区楼长与所属社区集合转换的JSON对象
     * @throws Exception SERVICE层异常
     */
    JSONArray findDormitoryManagersAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, List<Map<String, Object>> userData, String[] titles) throws Exception;

    /**
     * 通过社区楼长查找匹配的社区楼长
     *
     * @param systemUser        登录的系统用户对象
     * @param systemRoleId      系统角色编号
     * @param communityRoleId   社区角色编号
     * @param subdistrictRoleId 街道角色编号
     * @param dormitoryManager  需要查找的社区楼长
     * @param companyId         查找的范围单位的编号
     * @param companyRoleId     查找的范围单位的类别编号
     * @param pageNumber        分页页码
     * @param pageDataSize      每页展示的数量
     * @return 查找到的社区楼长集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findDormitoryManagerByDormitoryManager(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, DormitoryManager dormitoryManager, Long companyId, Long companyRoleId, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 设置Excel头部
     *
     * @return 设置接口
     */
    Map<String, Object> setExcelHead();

    /**
     * 通过社区编号查找最后一个编号
     *
     * @param communityId     社区编号
     * @param communityName   社区名称
     * @param subdistrictName 街道办事处名称
     * @return 最后一个编号
     * @throws Exception SERVICE层异常
     */
    String findLastId(Long communityId, String communityName, String subdistrictName) throws Exception;

    /**
     * 获取社区楼长录入统计信息
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
     * 获取社区楼长柱状图数据
     *
     * @param systemUser        正在登录中的系统用户对象
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param typeParam         类型参数
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 柱状图数据
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> getChartBar(SystemUser systemUser, Long companyId, Long companyType, Boolean typeParam, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) throws Exception;
}
