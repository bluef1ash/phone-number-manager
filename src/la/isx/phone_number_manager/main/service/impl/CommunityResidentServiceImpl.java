package la.isx.phone_number_manager.main.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import la.isx.phone_number_manager.main.entity.CommunityResident;
import la.isx.phone_number_manager.main.service.CommunityResidentService;
/**
 * 社区居民业务实现
 *
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
	/**
	 * 联系方式数组处理
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