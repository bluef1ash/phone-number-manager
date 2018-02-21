package www.action;

import annotation.RefreshCsrfToken;
import annotation.SystemUserAuth;
import annotation.VerifyCSRFToken;
import exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import utils.CommonUtil;
import www.entity.*;
import www.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CsrfTokenUtil;
import www.validator.SystemUserInputValidator;
import www.validator.UserPrivilegeInputValidator;
import www.validator.UserRoleInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户与用户角色控制器
 *
 * @author 廿二月的天
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
    @Resource
    private CommunityService communityService;
    @Resource
    private SubdistrictService subdistrictService;
    private final HttpServletRequest request;

    @Autowired
    public UserAndRoleAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("systemUserAddOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new SystemUserInputValidator(systemUserService, request));
        } else if ("systemUserRoleCreateOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new UserRoleInputValidator(userRoleService, request));
        } else if ("systemUserPrivilegeCreateOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new UserPrivilegeInputValidator(userPrivilegeService, request));
        }
    }

    /**
     * 系统用户列表
     *
     * @param model 前台模型
     * @param page  分页对象
     * @return 视图页面
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String systemUserList(Model model, Integer page) {
        try {
            Map<String, Object> systemUsers = systemUserService.findSystemUsersAndRoles(page, null);
            model.addAttribute("systemUsers", systemUsers.get("data"));
            model.addAttribute("pageInfo", systemUsers.get("pageInfo"));
            return "user/list";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 通过AJAX进行系统用户锁定与解锁
     *
     * @param session      session对象
     * @param systemUserId 系统用户编号
     * @param locked       锁定与解锁的标记
     * @return Ajax信息
     */
    @RequestMapping(value = "/user/ajax_user_lock", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Integer> systemUserLockedForAjax(HttpSession session, Integer systemUserId, Integer locked) {
        Map<String, Integer> map = new HashMap<>(2);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            Integer systemAdministratorId = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_administrator_id"));
            if (!systemUserId.equals(systemAdministratorId)) {
                SystemUser systemUser = new SystemUser();
                systemUser.setSystemUserId(systemUserId);
                systemUser.setIsLocked(locked);
                systemUserService.updateObject(systemUser);
                map.put("state", 1);
            } else {
                map.put("state", 0);
            }
        } catch (Exception e) {
            map.put("state", 0);
        }
        try {
            SystemUser user = systemUserService.findObject(systemUserId);
            map.put("isLocked", user.getIsLocked());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 添加系统用户
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/user/create", method = RequestMethod.GET)
    public String createSystemUser(Model model) {
        try {
            List<UserRole> userRoles = userRoleService.findObjects();
            model.addAttribute("userRoles", userRoles);
            return "user/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 修改系统用户
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
    public String editSystemUser(Model model, Integer id) {
        try {
            SystemUser user = systemUserService.findSystemUsersAndRoles(id);
            List<UserRole> userRoles = userRoleService.findObjects();
            model.addAttribute("user", user);
            model.addAttribute("userRoles", userRoles);
            return "user/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统用户
     *
     * @param request       HTTP响应对象
     * @param model         前台模型
     * @param systemUser    系统用户对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/user/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    @VerifyCSRFToken
    public String systemUserAddOrEditHandle(HttpServletRequest request, Model model, @Validated SystemUser systemUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "user/edit";
        }
        if ("POST".equals(request.getMethod())) {
            // 添加
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
        return "redirect:/system/user_role/user/list.action";
    }

    /**
     * 使用AJAX技术通过系统用户编号删除系统用户
     *
     * @param session session对象
     * @param id 对应编号
     * @return Ajax信息
     */
    @RequestMapping(value = "/user/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteSystemUserForAjax(HttpSession session, Integer id) {
        Map<String, Object> map = new HashMap<>(3);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            Integer systemAdministratorId = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_administrator_id"));
            if (id.equals(systemAdministratorId)) {
                map.put("state", 0);
                map.put("message", "不允许删除超级管理员！");
            } else {
                systemUserService.deleteObjectById(id);
                map.put("state", 1);
                map.put("message", "删除系统用户成功！");
            }
        } catch (Exception e) {
            map.put("state", 0);
            map.put("message", "删除系统用户失败！");
        }
        return map;
    }

    /**
     * 使用Ajax技术通过系统角色编号获取单位
     *
     * @param roleId 角色编号
     * @return 单位信息
     */
    @RequestMapping(value = "/user/ajax_get_company", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    List<?> getCompanyIdForAjax(String roleId) {
        try {
            List<?> companies = null;
            String[] role = URLDecoder.decode(roleId, "utf-8").split("-");
            if ("社区管理员".equals(role[1])) {
                companies = communityService.findObjectsForIdAndName();
            } else if ("街道管理员".equals(role[1])) {
                companies = subdistrictService.findObjectsForIdAndName();
            }
            List<Map<String, Object>> newCompanies = new ArrayList<>();
            if (companies != null) {
                for (Object object : companies) {
                    Map<String, Object> company = new HashMap<>(3);
                    if (object instanceof Subdistrict) {
                        company.put("value", ((Subdistrict) object).getSubdistrictId());
                        company.put("name", ((Subdistrict) object).getSubdistrictName());
                    } else {
                        company.put("value", ((Community) object).getCommunityId());
                        company.put("name", ((Community) object).getCommunityName());
                    }
                    newCompanies.add(company);
                }
            }
            return newCompanies;
        } catch (Exception e) {
            throw new BusinessException("获取单位失败！", e);
        }
    }

    /**
     * 使用Ajax技术获取所有系统用户
     *
     * @param session session对象
     * @return Ajax信息
     */
    @RequestMapping(value = "/user/ajax_get", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> getSystemUsersForAjax(HttpSession session) {
        Map<String, Object> map = new HashMap<>(4);
        try {
            List<SystemUser> systemUsers = systemUserService.findSystemUsers();
            map.put("state", 1);
            map.put("systemUsers", systemUsers);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", 0);
            map.put("message", "获取系统用户失败！");
        }
        map.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
        return map;
    }

    /**
     * 系统角色列表
     *
     * @param model 前台模型
     * @param page  分页页数
     * @return 视图页面
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String systemUserRoleList(Model model, Integer page) {
        try {
            Map<String, Object> userRoles = userRoleService.findObjects(page, null);
            model.addAttribute("userRoles", userRoles.get("data"));
            model.addAttribute("pageInfo", userRoles.get("pageInfo"));
            return "role/list";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加系统角色
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/role/create", method = RequestMethod.GET)
    public String createSystemUserRole(Model model) {
        try {
            List<UserRole> userRoles = userRoleService.findObjects();
            List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesAndSubPrivilegesAll();
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("userPrivileges", userPrivileges);
            return "role/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑系统角色
     *
     * @param model 前台模型
     * @param id    角色编号
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/role/edit", method = RequestMethod.GET)
    public String editSystemUserRole(Model model, Integer id) {
        try {
            List<UserRole> userRoles = userRoleService.findObjects();
            UserRole userRole = userRoleService.findObject(id);
            List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesAndSubPrivilegesAll();
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("userRole", userRole);
            model.addAttribute("userPrivileges", userPrivileges);
            return "role/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统角色
     *
     * @param request       HTTP响应对象
     * @param model         前台模型
     * @param userRole      系统角色对象
     * @param privilegeIds  系统权限编号数组
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/role/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    @VerifyCSRFToken
    public String systemUserRoleCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserRole userRole, Integer[] privilegeIds, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "role/edit";
        }
        if ("POST".equals(request.getMethod())) {
            // 添加
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
        return "redirect:/system/user_role/role/list.action";
    }

    /**
     * 使用AJAX技术通过系统用户角色编号删除系统用户角色
     *
     * @param session session对象
     * @param id 对应编号
     * @return Ajax信息
     */
    @RequestMapping(value = "/role/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteSystemUserRoleForAjax(HttpSession session, Integer id) {
        Map<String, Object> map = new HashMap<>(3);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            Integer systemRoleId = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_role_id"));
            if (id.equals(systemRoleId)) {
                UserRolePrivilege userRolePrivilege = new UserRolePrivilege();
                userRolePrivilege.setRoleId(id);
                userRolePrivilegeService.deleteUserRolePrivilegeByUserRolePrivilege(userRolePrivilege);
                userRoleService.deleteObjectById(id);
                map.put("state", 1);
                map.put("message", "删除用户角色成功！");
            } else {
                map.put("state", 0);
                map.put("message", "不允许删除系统用户角色！");
            }
        } catch (Exception e) {
            map.put("state", 0);
            map.put("message", "删除用户角色失败！");
        }
        return map;
    }

    /**
     * 系统权限列表
     *
     * @param model 前台模型对象
     * @param page  分页页码
     * @return 视图页面
     */
    @RequestMapping(value = "/privilege/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String systemUserPrivilegeList(Model model, Integer page) {
        try {
            Map<String, Object> userPrivileges = userPrivilegeService.findObjects(page, null);
            model.addAttribute("userPrivileges", userPrivileges.get("data"));
            model.addAttribute("pageInfo", userPrivileges.get("pageInfo"));
            return "privilege/list";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加系统权限
     *
     * @param model 前台模型对象
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/privilege/create", method = RequestMethod.GET)
    public String createSystemUserPrivilege(Model model) {
        List<UserPrivilege> userPrivileges;
        try {
            userPrivileges = userPrivilegeService.findObjects();
            model.addAttribute("userPrivileges", userPrivileges);
            return "privilege/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑系统权限
     *
     * @param model 前台模型对象
     * @param id    权限编号
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/privilege/edit", method = RequestMethod.GET)
    public String editSystemUserPrivilege(Model model, Integer id) {
        try {
            List<UserPrivilege> userPrivileges = userPrivilegeService.findObjects();
            UserPrivilege userPrivilege = userPrivilegeService.findObject(id);
            System.out.println(userPrivilege);
            model.addAttribute("userPrivileges", userPrivileges);
            model.addAttribute("userPrivilege", userPrivilege);
            return "privilege/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统权限
     *
     * @param request       HTTP响应对象
     * @param model         前台模型
     * @param userPrivilege 系统用户权限对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/privilege/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    @VerifyCSRFToken
    public String systemUserPrivilegeCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserPrivilege userPrivilege, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "privilege/edit";
        }
        if ("POST".equals(request.getMethod())) {
            // 添加
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
        return "redirect:/system/user_role/privilege/list.action";
    }

    /**
     * 使用AJAX技术通过系统用户权限编号删除系统用户权限
     *
     * @param id 系统用户权限编号
     * @return Ajax消息
     */
    @RequestMapping(value = "/privilege/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteSystemUserPrivilegeForAjax(Integer id) {
        Map<String, Object> map = new HashMap<>(3);
        try {
            userPrivilegeService.deleteObjectById(id);
            map.put("state", 1);
            map.put("message", "删除用户权限成功！");
        } catch (Exception e) {
            map.put("state", 0);
            map.put("message", "删除用户权限失败！");
        }
        return map;
    }

    /**
     * 通过AJAX技术获取系统用户角色拥有的权限
     *
     * @param session session对象
     * @param roleId  系统用户角色编号
     * @return Ajax消息
     */
    @SystemUserAuth(unAuth = true)
    @RequestMapping(value = "/privilege/ajax_get_privileges", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> getPrivilegesByRoleIdForAjax(HttpSession session, Integer roleId) {
        try {
            List<UserPrivilege> privileges = userPrivilegeService.findPrivilegesByRoleId(roleId);
            Map<String, Object> map = new HashMap<>(3);
            map.put("privileges", privileges);
            map.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return map;
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
