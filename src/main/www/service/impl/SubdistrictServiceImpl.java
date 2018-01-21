package www.service.impl;

import org.springframework.stereotype.Service;

import www.entity.Subdistrict;
import www.service.SubdistrictService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 街道业务实现
 *
 * @author 廿二月的天
 */
@Service("subdistrictService")
public class SubdistrictServiceImpl extends BaseServiceImpl<Subdistrict> implements SubdistrictService {
    @Override
    public Set<Subdistrict> findCommunitiesAndSubdistrictsByRole(Integer roleId, Integer roleLocationId) throws Exception {
        Set<Subdistrict> subdistricts;
        switch (roleId) {
            case 3:
                // 社区角色
                subdistricts = new HashSet<Subdistrict>();
                Subdistrict subdistrict = subdistrictsDao.selectSubdistrictAndCommunityByCommunityId(roleLocationId);
                subdistricts.add(subdistrict);
                break;
            case 2:
                // 街道角色
                subdistricts = subdistrictsDao.selectSubdistrictAndCommunityBySubdistrictId(roleLocationId);
                break;
            default:
                // 管理员角色
                subdistricts = subdistrictsDao.selectSubdistrictsAndCommunitiesAll();
                break;
        }
        return subdistricts;
    }

    @Override
    public List<Subdistrict> findSubdistrictBySubdistrict(Subdistrict subdistrict) throws Exception {
        return baseDao.selectObjectsByObject(subdistrict);
    }
}
