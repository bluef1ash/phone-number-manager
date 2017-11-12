package www.service;

import www.entity.Community;
import www.entity.Subdistrict;

import java.util.List;
import java.util.Set;

/**
 * 街道业务接口
 */
public interface SubdistrictService extends BaseService<Subdistrict> {
    /**
     * 通过角色查找街道及社区居委会
     *
     * @return
     */
    public Set<Subdistrict> findCommunitiesAndSubdistrictsByRole(Integer roleId, Integer roleLocationId) throws Exception;

    /**
     * 通过街道联系方式查找
     *
     * @param subdistrict
     * @return
     * @throws Exception
     */
    public List<Subdistrict> findSubdistrictBySubdistrict(Subdistrict subdistrict) throws Exception;
}
