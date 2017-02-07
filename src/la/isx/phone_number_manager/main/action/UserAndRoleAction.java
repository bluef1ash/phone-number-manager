package la.isx.phone_number_manager.main.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import la.isx.phone_number_manager.annotation.AvoidDuplicateSubmission;
import la.isx.phone_number_manager.annotation.SystemUserAuth;
import la.isx.phone_number_manager.exception.BusinessException;
import la.isx.phone_number_manager.main.entity.SystemUser;
import la.isx.phone_number_manager.main.entity.UserRole;
import la.isx.phone_number_manager.main.entity.UserRolePrivilege;
import la.isx.phone_number_manager.main.entity.UserPrivilege;
import la.isx.phone_number_manager.main.service.UserRoleService;
import la.isx.phone_number_manager.main.service.UserPrivilegeService;
import la.isx.phone_number_manager.main.service.UserRolePrivilegeService;
import la.isx.phone_number_manager.main.service.SystemUserService;

/**
 * 系统用户与用户角色控制器
 *
 */
@Controller
@SystemUserAuth
@RequestMapping("/system/user_role")
public class UserAndRoleAction {
	@Resource
	private SystemUserService systemUserService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private UserPrivilegeService userPrivilegeService;
	@Resource
	private UserRolePrivilegeService userRolePrivilegeService;
	/**
	 * 系统用户列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String systemUserList(Map<String, Object> map, Integer page) {
		try {
			Map<String, Object> systemUsers = systemUserService.findSystemUsersAndRoles(page, null);
			@SuppressWarnings("unchecked")
			PageInfo<SystemUser> pageInfo = (PageInfo<SystemUser>) systemUsers.get("pageInfo");
			map.put("systemUsers", systemUsers.get("data"));
			map.put("pageInfo", pageInfo);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "user/list";
	}
	/**
	 * 通过AJAX进行系统用户锁定与解锁
	 * 
	 * @param locked
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/user/ajax_user_lock", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> systemUserLockedForAjax(Integer locked, HttpSession session) {
		Map<String, Object> map = null;
		try {
			map = new HashMap<String, Object>();
			SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
			systemUser.setIsLocked(locked);
			int success = systemUserService.updateObject(systemUser);
			if (success > 0) {
				map.put("state", 1);
			} else {
				map.put("state", 0);
			}
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return map;
	}
	/**
	 * 添加系统用户
	 * 
	 * @param map
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/user/create", method = RequestMethod.GET)
	public String createSystemUser(Map<String, Object> map) {
		try {
			List<UserRole> userRoles = userRoleService.findObjects();
			map.put("userRoles", userRoles);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "user/create";
	}
	/**
	 * 修改系统用户
	 * 
	 * @param map
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/user/edit", method = RequestMethod.GET)
	public String editSystemUser(Map<String, Object> map, Integer id) {
		if (id != null) {
			try {
				SystemUser user = systemUserService.findSystemUsersAndRoles(id);
				List<UserRole> userRoles = userRoleService.findObjects();
				map.put("systemUser", user);
				map.put("userRoles", userRoles);
			} catch (Exception e) {
				throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
			}
		}
		return "user/edit";
	}
	/**
	 * 添加与修改处理系统用户
	 * @param map
	 * @param systemUser
	 * @param bindingResult
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/user/handle", method = RequestMethod.POST)
	public String systemUserAddOrEditHandle(Map<String, Object> map, @Validated SystemUser systemUser, BindingResult bindingResult) {
		if (systemUser != null) {
			if (systemUser.getSystemUserId() == null) {
				// 添加
				if (bindingResult.hasErrors()) {
					// 输出错误信息
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					map.put("messageErrors", allErrors);
					return "user/edit";
				}
				try {
					systemUserService.createSystemUser(systemUser);
				} catch (Exception e) {
					throw new BusinessException("添加用户失败！", e);
				}
			} else {
				// 修改
				try {
					systemUserService.updateSystemUser(systemUser);
				} catch (Exception e) {
					throw new BusinessException("修改用户失败！", e);
				}
			}
		}
		return "redirect:/system/user_role/user/list.action";
	}
	/**
	 * 使用AJAX技术通过系统用户ID删除系统用户
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/user/ajax_delete", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteSystemUserForAjax(Integer id) {
		Map<String, Object> map = null;
		try {
			systemUserService.deleteObjectById(id);
			map = new HashMap<String, Object>();
			map.put("message", 1);
			return map;
		} catch (Exception e) {
			throw new BusinessException("删除用户失败！", e);
		}
	}
	/**
	 * 系统角色列表
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/role/list", method = RequestMethod.GET)
	public String systemUserRoleList(Map<String, Object> map, Integer page) {
		try {
			Map<String, Object> userRoles = userRoleService.findObjects(page, null);
			@SuppressWarnings("unchecked")
			PageInfo<UserRole> pageInfo = (PageInfo<UserRole>) userRoles.get("pageInfo");
			map.put("userRoles", userRoles.get("data"));
			map.put("pageInfo", pageInfo);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "role/list";
	}
	/**
	 * 添加系统角色
	 * 
	 * @param map
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/role/create", method = RequestMethod.GET)
	public String createSystemUserRole(Map<String, Object> map) {
		try {
			List<UserRole> userRoles = userRoleService.findObjects();
			List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesAndsubPrivilegesAll();
			map.put("userRoles", userRoles);
			map.put("userPrivileges", userPrivileges);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "role/create";
	}
	/**
	 * 编辑系统角色
	 * 
	 * @param map
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/role/edit", method = RequestMethod.GET)
	public String editSystemUserRole(Map<String, Object> map, Integer id) {
		if (id != null) {
			try {
				List<UserRole> userRoles = userRoleService.findObjects();
				UserRole userRole = userRoleService.findObject(id);
				List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesAndsubPrivilegesAll();
				map.put("userRoles", userRoles);
				map.put("userRole", userRole);
				map.put("userPrivileges", userPrivileges);
			} catch (Exception e) {
				throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
			}
		}
		return "role/edit";
	}
	/**
	 * 添加与修改处理系统角色
	 * @param map
	 * @param userRole
	 * @param bindingResult
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/role/handle", method = RequestMethod.POST)
	public String systemUserRoleCreateOrEditHandle(Map<String, Object> map, @Validated UserRole userRole, Integer[] privilegeIds, String submit, BindingResult bindingResult) {
		if (userRole != null || privilegeIds.length > 1) {
			if ("添加".equals(submit)) {
				// 添加
				if (bindingResult.hasErrors()) {
					// 输出错误信息
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					map.put("messageErrors", allErrors);
					return "role/edit";
				}
				try {
					userRoleService.createObject(userRole);
					userRolePrivilegeService.addUserRolePrivileges(userRole, privilegeIds);
				} catch (Exception e) {
					throw new BusinessException("添加角色失败！", e);
				}
			} else {
				// 修改
				try {
					UserRolePrivilege oldUserRolePrivilege = new UserRolePrivilege();
					oldUserRolePrivilege.setRoleId(userRole.getRoleId());
					userRolePrivilegeService.deleteUserRolePrivilegeByUserRolePrivilege(oldUserRolePrivilege);
					userRoleService.updateObject(userRole);
					userRolePrivilegeService.addUserRolePrivileges(userRole, privilegeIds);
				} catch (Exception e) {
					throw new BusinessException("修改角色失败！", e);
				}
			}
		}
		return "redirect:/system/user_role/role/list.action";
	}
	/**
	 * 使用AJAX技术通过系统用户角色ID删除系统用户角色
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/role/ajax_delete", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteSystemUserRoleForAjax(Integer id) {
		Map<String, Object> map = null;
		try {
			UserRolePrivilege userRolePrivilege = new UserRolePrivilege();
			userRolePrivilege.setRoleId(id);
			userRolePrivilegeService.deleteUserRolePrivilegeByUserRolePrivilege(userRolePrivilege);
			userRoleService.deleteObjectById(id);
			map = new HashMap<String, Object>();
			map.put("message", 1);
			return map;
		} catch (Exception e) {
			throw new BusinessException("删除用户失败！", e);
		}
	}
	/**
	 * 系统权限列表
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/privilege/list", method = RequestMethod.GET)
	public String systemUserPrivilegeList(Map<String, Object> map, Integer page) {
		try {
			Map<String, Object> userPrivileges = userPrivilegeService.findObjects(page, null);
			@SuppressWarnings("unchecked")
			PageInfo<UserPrivilege> pageInfo = (PageInfo<UserPrivilege>) userPrivileges.get("pageInfo");
			map.put("userPrivileges", userPrivileges.get("data"));
			map.put("pageInfo", pageInfo);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "privilege/list";
	}
	/**
	 * 添加系统权限
	 * 
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/privilege/create", method = RequestMethod.GET)
	public String createSystemUserPrivilege(Map<String, List<UserPrivilege>> map) {
		List<UserPrivilege> userPrivileges;
		try {
			userPrivileges = userPrivilegeService.findObjects();
			map.put("userPrivileges", userPrivileges);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "privilege/create";
	}
	/**
	 * 编辑系统权限
	 * 
	 * @param map
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needSaveToken = true)
	@RequestMapping(value = "/privilege/edit", method = RequestMethod.GET)
	public String editSystemUserPrivilege(Map<String, Object> map, Integer id) {
		if (id != null) {
			try {
				List<UserPrivilege> userPrivileges = userPrivilegeService.findObjects();
				UserPrivilege userPrivilege = userPrivilegeService.findObject(id);
				System.out.println(userPrivilege);
				map.put("userPrivileges", userPrivileges);
				map.put("userPrivilege", userPrivilege);
			} catch (Exception e) {
				throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
			}
		}
		return "privilege/edit";
	}
	/**
	 * 添加与修改处理系统权限
	 * @param map
	 * @param userRole
	 * @param bindingResult
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/privilege/handle", method = RequestMethod.POST)
	public String systemUserPrivilegeCreateOrEditHandle(Map<String, Object> map, @Validated UserPrivilege userPrivilege, BindingResult bindingResult) {
		if (userPrivilege != null) {
			if (userPrivilege.getPrivilegeId() == null) {
				// 添加
				if (bindingResult.hasErrors()) {
					// 输出错误信息
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					map.put("messageErrors", allErrors);
					return "privilege/edit";
				}
				try {
					userPrivilegeService.createObject(userPrivilege);
				} catch (Exception e) {
					throw new BusinessException("添加权限失败！", e);
				}
			} else {
				// 修改
				try {
					userPrivilegeService.updateObject(userPrivilege);
				} catch (Exception e) {
					throw new BusinessException("修改权限失败！", e);
				}
			}
		}
		return "redirect:/system/user_role/privilege/list.action";
	}
	/**
	 * 使用AJAX技术通过系统用户权限ID删除系统用户权限
	 * @param id
	 * @return
	 */
	@AvoidDuplicateSubmission(needRemoveToken = true)
	@RequestMapping(value = "/privilege/ajax_delete", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteSystemUserPrivilegeForAjax(Integer id) {
		Map<String, Object> map = null;
		try {
			userPrivilegeService.deleteObjectById(id);
			map = new HashMap<String, Object>();
			map.put("message", 1);
			return map;
		} catch (Exception e) {
			throw new BusinessException("删除用户失败！", e);
		}
	}
	/**
	 * 通过AJAX技术获取系统用户角色拥有的权限
	 * @param roleId
	 * @return
	 */
	@SystemUserAuth(unAuth = true)
	@RequestMapping(value = "/privilege/ajax_get_privileges", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> ajaxGetPrivilegesByRoleId(Integer roleId) {
		Map<String, Object> map = null;
		try {
			List<UserPrivilege> privileges = userPrivilegeService.findPrivilegesByRoleId(roleId);
			map = new HashMap<String, Object>();
			map.put("privileges", privileges);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return map;
	}
}