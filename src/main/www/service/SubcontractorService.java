package www.service;

import www.entity.Subcontractor;

import java.util.List;
import java.util.Map;

/**
 * 社区分包人Service接口
 *
 * @author 廿二月的天
 */
public interface SubcontractorService extends BaseService<Subcontractor> {
    /**
     * 通过系统用户角色编号与系统用户角色定位编号分页查找
     *
     * @param pageNumber        分页页码
     * @param pageDataSize      每页展示数据数量
     * @param roleId            系统用户角色编号
     * @param roleLocationId    系统用户角色定位编号
     * @param configurationsMap 系统配置项
     * @return 社区分包人对象集合与分页对象
     * @throws Exception Service异常
     */
    Map<String, Object> findSubcontractors(Integer pageNumber, Integer pageDataSize, Long roleId, Long roleLocationId, Map<String, Object> configurationsMap) throws Exception;

    /**
     * 通过系统用户角色查找
     *
     * @param roleId            系统用户角色编号
     * @param roleLocationId    系统用户角色定位编号
     * @param configurationsMap 系统配置项
     * @return 社区分包人对象集合
     * @throws Exception Service异常
     */
    List<Subcontractor> findSubcontractors(Long roleId, Long roleLocationId, Map<String, Object> configurationsMap) throws Exception;

    /**
     * 通过社区编号查找
     *
     * @param communityId 社区编号
     * @return 社区分包人对象集合
     * @throws Exception Service异常
     */
    List<Subcontractor> findSubcontractors(Long communityId) throws Exception;
}
