package main.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import main.entity.Community;
import org.springframework.stereotype.Service;

import main.entity.CommunityResident;
import main.service.CommunityResidentService;

/**
 * 社区居民业务实现
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResident> implements CommunityResidentService {
    @Override
    public Map<String, Object> findCommunityResidentAndCommunityById(Integer id) throws Exception {
        CommunityResident communityResident = communityResidentsDao.selectCommunityResidentAndCommunityById(id);
        String[] residentPhones = communityResident.getCommunityResidentPhones().split(",");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("communityResident", communityResident);
        map.put("residentPhones", residentPhones);
        return map;
    }

    @Override
    public int createCommuntyResident(CommunityResident communityResident, String[] residentPhones) throws Exception {
        multiplePhoneHandle(communityResident, residentPhones);
        return baseDao.insertObject(communityResident);
    }

    @Override
    public int updateCommuntyResident(CommunityResident communityResident, String[] residentPhones) throws Exception {
        multiplePhoneHandle(communityResident, residentPhones);
        return baseDao.updateObject(communityResident);
    }

    @Override
    public Map<String, Object> findCommunityResidentByCommunityResident(CommunityResident communityResident, Integer pageNum, String unit, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        String[] communities = unit.split("\\|");
        List<CommunityResident> communityResidents = null;
        if (Integer.parseInt(communities[1]) == 1) {
            communityResident.setCommunityId(Integer.parseInt(communities[0]));
            communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
        } else {
            List<Community> newCommunities = communitiesDao.selectCommunitiesBySubdistrictId(Integer.parseInt(communities[0]));
            int communitiesLength = newCommunities.size();
            Integer[] communityIds = new Integer[communitiesLength];
            for (int i = 0; i < communitiesLength; i++) {
                communityIds[i] = newCommunities.get(i).getCommunityId();
            }
            communityResident.setCommunityIds(communityIds);
            communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResidentAndCommunityIds(communityResident);
        }
        return findObjectsMethod(communityResidents, pageNum, pageSize);
    }

    /**
     * 联系方式数组处理
     *
     * @param communityResident
     * @param residentPhones
     */
    private void multiplePhoneHandle(CommunityResident communityResident, String[] residentPhones) {
        StringBuilder tempPhone = new StringBuilder();
        for (String residentPhone : residentPhones) {
            if (residentPhone != null && !"".equals(residentPhone)) {
                tempPhone.append(residentPhone).append(",");
            }
        }
        communityResident.setCommunityResidentPhones(tempPhone.toString().substring(0, tempPhone.length() - 1));
        communityResident.setCommunityResidentEditTime(new Timestamp(new Date().getTime()));
    }
}