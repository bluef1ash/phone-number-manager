package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.*;
import com.github.phonenumbermanager.validator.SystemUserInputValidator;
import com.github.phonenumbermanager.validator.UserPrivilegeInputValidator;
import com.github.phonenumbermanager.validator.UserRoleInputValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统用户与用户角色控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/system/user_role")
public class UserAndRoleAction extends BaseAction {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserPrivilegeService userPrivilegeService;
    @Resource
    private UserRolePrivilegeService userRolePrivilegeService;
    @Resource
    private SubdistrictService subdistrictService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        if (requestUrl.toString().contains("/user/handle")) {
            binder.replaceValidators(new SystemUserInputValidator(systemUserService, request));
        } else if (requestUrl.toString().contains("/role/handle")) {
            binder.replaceValidators(new UserRoleInputValidator(userRoleService, request));
        } else if (requestUrl.toString().contains("/privilege/handle")) {
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
    @GetMapping("/user/list")
    public String systemUserList(HttpSession session, Model model, Integer page) {
        getSessionRoleId(session);
        try {
            Map<String, Object> systemUsers = systemUserService.findCorrelation(page, null);
            model.addAttribute("systemAdministratorId", systemAdministratorId);
            model.addAttribute("systemUsers", JSON.toJSON(systemUsers.get("data")));
            model.addAttribute("pageInfo", systemUsers.get("pageInfo"));
            return "user/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 通过AJAX进行系统用户锁定与解锁
     *
     * @param session session对象
     * @param id      系统用户编号
     * @param locked  锁定与解锁的标记
     * @return Ajax信息
     */
    @PostMapping("/user/ajax_user_lock")
    @ResponseBody
    public Map<String, Object> systemUserLockedForAjax(HttpSession session, @RequestParam Long id, @RequestParam Boolean locked) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        getSessionRoleId(session);
        SystemUser systemUser = new SystemUser();
        systemUser.setId(id);
        systemUser.setLocked(locked);
        try {
            if (!id.equals(systemAdministratorId)) {
                systemUserService.update(systemUser);
                jsonMap.put("state", 1);
                SystemUser user = systemUserService.find(id);
                jsonMap.put("locked", user.getLocked());
            } else {
                jsonMap.put("state", 0);
            }
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException(e);
        }
    }

    /**
     * 添加系统用户
     *
     * @param session Session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @GetMapping("/user/create")
    public String createSystemUser(HttpSession session, Model model) {
        getSessionRoleId(session);
        model.addAttribute("communityCompanyType", communityCompanyType);
        model.addAttribute("subdistrictCompanyType", subdistrictCompanyType);
        model.addAttribute("systemAdministratorId", systemAdministratorId);
        try {
            List<UserRole> userRoles = userRoleService.find();
            List<Subdistrict> subdistricts = subdistrictService.find();
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("subdistricts", subdistricts);
            return "user/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 修改系统用户
     *
     * @param session Session对象
     * @param model   前台模型
     * @param id      编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/user/edit")
    public String editSystemUser(HttpSession session, Model model, @RequestParam Long id) {
        getSessionRoleId(session);
        model.addAttribute("communityCompanyType", communityCompanyType);
        model.addAttribute("subdistrictCompanyType", subdistrictCompanyType);
        model.addAttribute("systemAdministratorId", systemAdministratorId);
        try {
            List<UserRole> userRoles = userRoleService.find();
            SystemUser user = systemUserService.findCorrelation(id);
            List<Subdistrict> subdistricts = subdistrictService.find();
            model.addAttribute("user", user);
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("subdistricts", subdistricts);
            return "user/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统用户
     *
     * @param session       Session对象
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param systemUser    系统用户对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/user/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String systemUserAddOrEditHandle(HttpSession session, HttpServletRequest request, Model model, @Validated SystemUser systemUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("communityCompanyType", communityCompanyType);
            model.addAttribute("subdistrictCompanyType", subdistrictCompanyType);
            model.addAttribute("systemAdministratorId", systemAdministratorId);
            model.addAttribute("messageErrors", allErrors);
            return "user/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            try {
                systemUserService.create(systemUser);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加用户失败！", e);
            }
        } else {
            // 修改
            try {
                systemUserService.update(systemUser);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改用户失败！", e);
            }
        }
        return "redirect:/system/user_role/user/list";
    }

    /**
     * 使用AJAX技术通过系统用户编号删除系统用户
     *
     * @param session session对象
     * @param id      对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/user/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteSystemUserForAjax(HttpSession session, @RequestParam Long id) {
        getSessionRoleId(session);
        Map<String, Object> map = new HashMap<>(3);
        try {
            if (id.equals(systemAdministratorId)) {
                map.put("state", 0);
                map.put("message", "不允许删除超级管理员！");
            } else {
                systemUserService.delete(id);
                map.put("state", 1);
                map.put("message", "删除系统用户成功！");
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除系统用户失败！", e);
        }
    }

    /**
     * 使用Ajax技术获取所有系统用户
     *
     * @return Ajax信息
     */
    @GetMapping("/user/ajax_get")
    @ResponseBody
    public Map<String, Object> getSystemUsersForAjax() {
        Map<String, Object> map = new HashMap<>(3);
        try {
            List<SystemUser> systemUsers = systemUserService.findIdAndName();
            map.put("state", 1);
            map.put("systemUsers", systemUsers);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("获取系统用户失败！", e);
        }
    }

    /**
     * 系统角色列表
     *
     * @param model 前台模型
     * @param page  分页页数
     * @return 视图页面
     */
    @GetMapping("/role/list")
    public String systemUserRoleList(Model model, Integer page) {
        try {
            Map<String, Object> userRoles = userRoleService.find(page, null);
            model.addAttribute("userRoles", userRoles.get("data"));
            model.addAttribute("pageInfo", userRoles.get("pageInfo"));
            return "role/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加系统角色
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @GetMapping("/role/create")
    public String createSystemUserRole(Model model) {
        try {
            List<UserRole> userRoles = userRoleService.find();
            Set<UserPrivilege> userPrivileges = userPrivilegeService.findForSub();
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("userPrivileges", userPrivileges);
            return "role/edit";
        } catch (Exception e) {
            e.printStackTrace();
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
    @GetMapping("/role/edit")
    public String editSystemUserRole(Model model, @RequestParam Long id) {
        try {
            List<UserRole> userRoles = userRoleService.find();
            UserRole userRole = userRoleService.findCorrelation(id);
            Set<UserPrivilege> userPrivileges = userPrivilegeService.findForSub();
            model.addAttribute("userRoles", userRoles);
            model.addAttribute("userRole", userRole);
            model.addAttribute("userPrivileges", userPrivileges);
            return "role/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统角色
     *
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param userRole      系统角色对象
     * @param privilegeIds  系统权限编号数组
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/role/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String systemUserRoleCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserRole userRole, @RequestParam Long[] privilegeIds, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "role/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            try {
                userRoleService.create(userRole);
                userRolePrivilegeService.create(userRole, privilegeIds);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加角色失败！", e);
            }
        } else {
            // 修改
            try {
                UserRolePrivilege oldUserRolePrivilege = new UserRolePrivilege();
                oldUserRolePrivilege.setRoleId(userRole.getId());
                userRolePrivilegeService.delete(oldUserRolePrivilege);
                userRoleService.update(userRole);
                userRolePrivilegeService.create(userRole, privilegeIds);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改角色失败！", e);
            }
        }
        return "redirect:/system/user_role/role/list";
    }

    /**
     * 使用AJAX技术通过系统用户角色编号删除系统用户角色
     *
     * @param session session对象
     * @param id      对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/role/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteSystemUserRoleForAjax(HttpSession session, @RequestParam Long id) {
        getSessionRoleId(session);
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            userRoleService.delete(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除用户角色成功！");
            return jsonMap;
        } catch (BusinessException be) {
            be.printStackTrace();
            throw new JsonException(be.getMessage(), be);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除用户角色失败！", e);
        }
    }

    /**
     * 系统权限列表
     *
     * @param model 前台模型对象
     * @param page  分页页码
     * @return 视图页面
     */
    @GetMapping("/privilege/list")
    public String systemUserPrivilegeList(Model model, Integer page) {
        try {
            Map<String, Object> userPrivileges = userPrivilegeService.find(page, null);
            model.addAttribute("userPrivileges", userPrivileges.get("data"));
            model.addAttribute("pageInfo", userPrivileges.get("pageInfo"));
            return "privilege/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加系统权限
     *
     * @param model 前台模型对象
     * @return 视图页面
     */
    @GetMapping("/privilege/create")
    public String createSystemUserPrivilege(Model model) {
        List<UserPrivilege> userPrivileges;
        try {
            userPrivileges = userPrivilegeService.findAndHandler();
            model.addAttribute("userPrivileges", userPrivileges);
            return "privilege/edit";
        } catch (Exception e) {
            e.printStackTrace();
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
    @GetMapping("/privilege/edit")
    public String editSystemUserPrivilege(Model model, @RequestParam Long id) {
        try {
            List<UserPrivilege> userPrivileges = userPrivilegeService.find();
            UserPrivilege userPrivilege = userPrivilegeService.find(id);
            model.addAttribute("userPrivileges", userPrivileges);
            model.addAttribute("userPrivilege", userPrivilege);
            return "privilege/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加与修改处理系统权限
     *
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param userPrivilege 系统用户权限对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/privilege/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String systemUserPrivilegeCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserPrivilege userPrivilege, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "privilege/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            try {
                userPrivilegeService.create(userPrivilege);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加权限失败！", e);
            }
        } else {
            // 修改
            try {
                userPrivilegeService.update(userPrivilege);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改权限失败！", e);
            }
        }
        return "redirect:/system/user_role/privilege/list";
    }

    /**
     * 使用AJAX技术通过系统用户权限编号删除系统用户权限
     *
     * @param id 系统用户权限编号
     * @return Ajax消息
     */
    @DeleteMapping("/privilege/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteSystemUserPrivilegeForAjax(@RequestParam Long id) {
        Map<String, Object> map = new HashMap<>(3);
        try {
            userPrivilegeService.delete(id);
            map.put("state", 1);
            map.put("message", "删除用户权限成功！");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除用户权限失败！", e);
        }
    }

    /**
     * 通过AJAX技术获取系统用户角色拥有的权限
     *
     * @param roleId 系统用户角色编号
     * @return Ajax消息
     */
    @GetMapping("/privilege/ajax_get_privileges")
    @ResponseBody
    public Map<String, Object> getPrivilegesByRoleIdForAjax(@RequestParam Long roleId) {
        try {
            Set<UserPrivilege> privileges = userPrivilegeService.findByRoleId(roleId);
            Map<String, Object> map = new HashMap<>(2);
            map.put("privileges", privileges);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
