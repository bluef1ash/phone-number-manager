package www.service.impl;

import org.springframework.stereotype.Service;
import utils.CommonUtil;
import www.entity.Subcontractor;
import www.service.SubcontractorService;

import java.util.List;
import java.util.Map;

/**
 * 社区分包人Service实现
 *
 * @author 廿二月的天
 */
@Service("subcontractorService")
public class SubcontractorServiceImpl extends BaseServiceImpl<Subcontractor> implements SubcontractorService {
    @Override
    public Map<String, Object> findSubcontractors(Integer pageNumber, Integer pageDataSize, Long roleId, Long roleLocationId, Long communityRoleId, Long subdistrictRoleId, Long systemRoleId) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<Subcontractor> subcontractors = findSubcontractorBySystemUserRole(roleId, roleLocationId, communityRoleId, subdistrictRoleId, systemRoleId);
        return findObjectsMethod(subcontractors);
    }

    @Override
    public List<Subcontractor> findSubcontractors(Long roleId, Long roleLocationId, Map<String, Object> configurationsMap) throws Exception {
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        Long systemRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_role_id"));
        return findSubcontractorBySystemUserRole(roleId, roleLocationId, communityRoleId, subdistrictRoleId, systemRoleId);
    }

    @Override
    public List<Subcontractor> findSubcontractors(Long communityId) throws Exception {
        return subcontractorsDao.selectSubcontractors(communityId);
    }

    /**
     * 通过系统用户角色查找社区分包人
     *
     * @param roleId            系统用户角色编号
     * @param roleLocationId    系统用户角色定位编号
     * @param communityRoleId   社区用户角色编号
     * @param subdistrictRoleId 街道用户角色编号
     * @param systemRoleId      系统用户角色编号
     * @return 对应的社区分包人对象集合
     */
    private List<Subcontractor> findSubcontractorBySystemUserRole(Long roleId, Long roleLocationId, Long communityRoleId, Long subdistrictRoleId, Long systemRoleId) {
        List<Subcontractor> subcontractors = null;
        if (roleId.equals(communityRoleId)) {
            subcontractors = subcontractorsDao.selectSubcontractors(roleLocationId);
        } else if (roleId.equals(subdistrictRoleId)) {
            subcontractors = subcontractorsDao.selectSubcontractorsBySubdistrictId(roleLocationId);
        } else if (roleId.equals(systemRoleId)) {
            subcontractors = subcontractorsDao.selectSubcontractorsAll();
        }
        return subcontractors;
    }
}
