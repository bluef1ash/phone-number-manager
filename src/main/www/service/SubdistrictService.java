package www.service;

import www.entity.Subdistrict;

import java.util.Set;

/**
 * 街道业务接口
 *
 */
public interface SubdistrictService extends BaseService<Subdistrict> {
    /**
     * 通过角色查找街道及社区居委会
     * @return
     */
    public Set<Subdistrict> findCommunitiesAndSubdistrictsByRole(Integer roleId, Integer roleLocationId) throws Exception;
}
