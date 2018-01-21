package www.service;

import www.entity.Subdistrict;

import java.util.List;
import java.util.Set;

/**
 * 街道业务接口
 *
 * @author 廿二月的天
 */
public interface SubdistrictService extends BaseService<Subdistrict> {
    /**
     * 通过角色查找街道及社区居委会
     *
     * @param roleId         系统用户角色编号
     * @param roleLocationId 系统用户角色定位编号
     * @return 查找到的街道及社区居委会的集合
     * @throws Exception SERVICE层异常
     */
    Set<Subdistrict> findCommunitiesAndSubdistrictsByRole(Integer roleId, Integer roleLocationId) throws Exception;

    /**
     * 通过街道联系方式查找
     *
     * @param subdistrict 需要查找的街道办事处信息对象
     * @return 查找到的街道办事处对象集合
     * @throws Exception SERVICE层异常
     */
    List<Subdistrict> findSubdistrictBySubdistrict(Subdistrict subdistrict) throws Exception;
}
