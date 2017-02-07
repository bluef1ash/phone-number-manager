package la.isx.phone_number_manager.main.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import la.isx.phone_number_manager.main.entity.UserPrivilege;
import la.isx.phone_number_manager.main.service.UserPrivilegeService;

/**
 * 系统用户权限业务实现
 *
 */
@Service("userPrivilegeService")
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilege> implements UserPrivilegeService {
	@Override
	public List<UserPrivilege> findPrivilegesByRoleId(Integer roleId) throws Exception {
		return userPrivilegesDao.selectPrivilegesByRoleId(roleId);
	}
	@Override
	public List<UserPrivilege> findPrivilegesByIsDisplay(Integer isDisplay, HttpSession session) throws Exception {
		@SuppressWarnings("unchecked")
		Set<String> privilegeAuthes = (Set<String>) ((Map<String, Object>) session.getAttribute("privilegeMap")).get("privilegeAuth");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDisplay", isDisplay);
		map.put("constraintAuthes", privilegeAuthes);
		List<UserPrivilege> userPrivileges = userPrivilegesDao.selectPrivilegesByIsDisplayAndConstraintAuth(map);
//		findPrivilegesAndsubPrivileges(userPrivileges);
		return findPrivilegesAndsubPrivileges(userPrivileges, isDisplay); //userPrivileges;
	}
	/**
	 * 有序排列用户权限
	 * 注意：需要改成递归算法！！！！！！！！
	 * @param userPrivileges
	 * @return 
	 * @throws Exception 
	 */
	private List<UserPrivilege> findPrivilegesAndsubPrivileges(List<UserPrivilege> userPrivileges, Integer isDisplay) throws Exception {
		List<UserPrivilege> subUserPrivileges = null;
		Map<Integer, UserPrivilege> newUserPrivilegesMap = new HashMap<Integer, UserPrivilege>();
		UserPrivilege tempPrivilege = null;
		for (UserPrivilege userPrivilege : userPrivileges) {
			if (userPrivilege.getHigherPrivilege() == 0) { // 顶级分类
				subUserPrivileges = userPrivilegesDao.selectPrivilegesByHigherPrivilegeAndIsDisplay(userPrivilege.getPrivilegeId(), isDisplay);
				userPrivilege.setSubUserPrivileges(subUserPrivileges);
				newUserPrivilegesMap.put(userPrivilege.getPrivilegeId(), userPrivilege);
			} else {
				if (subUserPrivileges != null && subUserPrivileges.size() > 0) { // 第一次加入子分类对象
					if (subUserPrivileges.get(0).getHigherPrivilege() != userPrivilege.getHigherPrivilege()) { // 判断是否为同一父分类
						tempPrivilege = newUserPrivilegesMap.get(subUserPrivileges.get(0).getHigherPrivilege());
						if (tempPrivilege.getSubUserPrivileges() != null) {
							tempPrivilege.setSubUserPrivileges(subUserPrivileges);
						}
						subUserPrivileges = new ArrayList<UserPrivilege>();
					}
				} else {
					subUserPrivileges = new ArrayList<UserPrivilege>();
				}
				subUserPrivileges.add(userPrivilege);
			}
		}
		if (subUserPrivileges != null && subUserPrivileges.size() > 0) {
			newUserPrivilegesMap.get(subUserPrivileges.get(0).getHigherPrivilege()).setSubUserPrivileges(subUserPrivileges);
		}
		return new ArrayList<UserPrivilege>(newUserPrivilegesMap.values());
	}
	@Override
	public List<UserPrivilege> findPrivilegesAndsubPrivilegesAll() throws Exception {
		List<UserPrivilege> userPrivileges = baseDao.selectObjectsAll();
//		findPrivilegesAndsubPrivileges(userPrivileges);
		return findPrivilegesAndsubPrivileges(userPrivileges, null);// userPrivileges;
	}
}