package main.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.PageHelper;
import constant.SystemConstant;
import org.springframework.stereotype.Service;

import main.entity.SystemUser;
import main.entity.UserPrivilege;
import main.service.SystemUserService;
import utils.CommonUtil;
import utils.DateUtil;
/**
 * 系统用户Service实现
 *
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUser> implements SystemUserService {

	@Override
	public Map<String, Object> loginCheck(HttpServletRequest request, SystemUser systemUser, String captcha, String sRand) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", -1);
		if (captcha == null || captcha.isEmpty()) {
			//得到用户读入框信息，如果没有输入或者为空，直接跳转到验证失败页面
			map.put("message", "验证码不能为空！");
		} else {
			if (!sRand.equalsIgnoreCase((captcha))) {
				//得到用户输入的验证码匹配成功，跳转到验证通过页面
				map.put("message", "验证码错误！");
			} else {
                map.put("state", 0);
				//得到用户输入的验证码匹配失败，跳转到验证失败页面
				if (systemUser != null) {
					String passwordMd5 = CommonUtil.MD5(systemUser.getPassword());
					SystemUser user = systemUsersDao.selectSystemUserAndRoleAndPrivilegesByName(systemUser.getUsername());
					map.put("message", "登录失败！请检查用户名或密码！");
					if (user != null) {
						if (passwordMd5 != null && passwordMd5.equals(user.getPassword())) {
							if (user.getIsLocked() == 1) {
								map.put("message", "登录失败！" + user.getUsername() + "已被锁定，请联系管理员！");
							} else {
								user.setLoginTime(DateUtil.getDateNow(new Date()));
								user.setLoginIp(CommonUtil.getIp(request));
								baseDao.updateObject(user);
								user.setPassword(null);
								Set<String> privilegeAuth = new HashSet<String>();
								Set<String> privilegeParents = new HashSet<String>();
								Map<String, Set<String>> privilegeMap = new HashMap<String, Set<String>>();
                                Set<UserPrivilege> userPrivileges = null;
                                if (user.getSystemUserId() == SystemConstant.SYSTEM_ADMINISTRATOR_ID) {
                                    userPrivileges = new HashSet<UserPrivilege>(userPrivilegesDao.selectPrivilegesByHigherPrivilegeAndIsDisplay(0, 1));
                                } else {
                                    userPrivileges = user.getUserRole().getUserPrivileges();
                                }
								for (UserPrivilege userPrivilege : userPrivileges) {
									Integer higherPrivilegeId = userPrivilege.getHigherPrivilege();
									privilegeAuth.add(userPrivilege.getConstraintAuth());
									if (higherPrivilegeId != 0) {
										UserPrivilege privilege = userPrivilegesDao.selectObjectById(higherPrivilegeId);
										privilegeParents.add(privilege.getConstraintAuth());
									}
								}
								privilegeMap.put("privilegeAuth", privilegeAuth);
								privilegeMap.put("privilegeParents", privilegeParents);
								map.put("state", 1);
								map.put("message", "登录成功！");
								map.put("systemUser", user);
								map.put("privilegeMap", privilegeMap);
							}
						}
					} else {
						map.put("message", "登录失败！找不到" + systemUser.getUsername() + "用户");
					}
				}
			}
		}
		return map;
	}
	@Override
	public int createSystemUser(SystemUser systemUser) throws Exception {
		if (systemUser != null) {
			if (systemUser.getPassword() != null && !"".equals(systemUser.getPassword())) {
				systemUser.setPassword(CommonUtil.MD5(systemUser.getPassword()));
			}
			return baseDao.insertObject(systemUser);
		}
		return 0;
	}
	@Override
	public int updateSystemUser(SystemUser systemUser) throws Exception {
		if (systemUser != null) {
			if (systemUser.getPassword() != null && !"".equals(systemUser.getPassword())) {
				systemUser.setPassword(CommonUtil.MD5(systemUser.getPassword()));
			}
			return baseDao.updateObject(systemUser);
		}
		return 0;
	}
	@Override
	public Map<String, Object> findSystemUsersAndRoles(Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
		List<SystemUser> data = systemUsersDao.selectSystemUsersAndRolesAll();
		return findObjectsMethod(data, pageNum, pageSize);
	}
	@Override
	public SystemUser findSystemUsersAndRoles(Integer id) throws Exception {
		return systemUsersDao.selectSystemUserAndRoleById(id);
	}
}
