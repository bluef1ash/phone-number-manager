package com.github.phonenumbermanager.action;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        String s = requestUrl.substring(requestUrl.lastIndexOf("/"));
        boolean isUser = "user".equals(s) && (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod()));
        boolean isRole = "role".equals(s) && (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod()));
        boolean isPrivilege = "privilege".equals(s) && (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod()));
        if (isUser) {
            binder.replaceValidators(new SystemUserInputValidator(systemUserService, request));
        } else if (isRole) {
            binder.replaceValidators(new UserRoleInputValidator(userRoleService, request));
        } else if (isPrivilege) {
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
    @GetMapping({"/user", "/user/{page}"})
    public String systemUserList(HttpSession session, Model model, @PathVariable(required = false) Integer page) {
        getSessionRoleId(session);
        model.addAttribute("systemAdministratorId", systemAdministratorId);
        model.addAttribute("systemUsers", systemUserService.getCorrelation(page, null));
        return "user/list";
    }

    /**
     * 通过AJAX进行系统用户锁定与解锁
     *
     * @param session session对象
     * @param id      系统用户编号
     * @param locked  锁定与解锁的标记
     * @return Ajax信息
     */
    @PostMapping("/user/lock")
    @ResponseBody
    public Map<String, Object> systemUserLockedForAjax(HttpSession session, @RequestParam Long id, @RequestParam Boolean locked) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        getSessionRoleId(session);
        SystemUser systemUser = new SystemUser();
        systemUser.setId(id);
        systemUser.setLocked(locked);
        if (!id.equals(systemAdministratorId)) {
            systemUserService.updateById(systemUser);
            jsonMap.put("state", 1);
            SystemUser user = systemUserService.getById(id);
            jsonMap.put("locked", user.getLocked());
        } else {
            jsonMap.put("state", 0);
        }
        return jsonMap;
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
        List<UserRole> userRoles = userRoleService.list();
        List<Subdistrict> subdistricts = subdistrictService.list();
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("subdistricts", subdistricts);
        return "user/edit";
    }

    /**
     * 修改系统用户
     *
     * @param session Session对象
     * @param model   前台模型
     * @param id      编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/user/edit/{id}")
    public String editSystemUser(HttpSession session, Model model, @PathVariable Long id) {
        getSessionRoleId(session);
        model.addAttribute("communityCompanyType", communityCompanyType);
        model.addAttribute("subdistrictCompanyType", subdistrictCompanyType);
        model.addAttribute("systemAdministratorId", systemAdministratorId);
        if (id == null) {
            id = systemUser.getId();
        }
        List<UserRole> userRoles = userRoleService.list();
        SystemUser user = systemUserService.getCorrelation(id);
        List<Subdistrict> subdistricts = subdistrictService.list();
        model.addAttribute("user", user);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("subdistricts", subdistricts);
        return "user/edit";
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
    @RequestMapping(value = "/user", method = {RequestMethod.POST, RequestMethod.PUT})
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
            if (!systemUserService.save(systemUser)) {
                throw new BusinessException("添加用户失败！");
            }
        } else {
            // 修改
            if (!systemUserService.updateById(systemUser)) {
                throw new BusinessException("修改用户失败！");
            }
        }
        return "redirect:/system/user_role/user";
    }

    /**
     * 使用AJAX技术通过系统用户编号删除系统用户
     *
     * @param session session对象
     * @param id      对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/user/{id}")
    @ResponseBody
    public Map<String, Object> deleteSystemUserForAjax(HttpSession session, @PathVariable Long id) {
        getSessionRoleId(session);
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (id.equals(systemAdministratorId)) {
            jsonMap.put("state", 0);
            jsonMap.put("message", "不允许删除超级管理员！");
        } else {
            if (!systemUserService.removeById(id)) {
                throw new JsonException("删除系统用户失败！");
            }
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除系统用户成功！");
        }
        return jsonMap;
    }

    /**
     * 使用Ajax技术获取所有系统用户
     *
     * @return Ajax信息
     */
    @GetMapping("/user/load")
    @ResponseBody
    public Map<String, Object> getSystemUsersForAjax() {
        Map<String, Object> jsonMap = new HashMap<>(3);
        List<SystemUser> systemUsers = systemUserService.getIdAndName();
        jsonMap.put("state", 1);
        jsonMap.put("systemUsers", systemUsers);
        return jsonMap;
    }

    /**
     * 系统角色列表
     *
     * @param model 前台模型
     * @param page  分页页数
     * @return 视图页面
     */
    @GetMapping({"/role", "/role/{page}"})
    public String systemUserRoleList(Model model, @PathVariable(required = false) Integer page) {
        model.addAttribute("userRoles", userRoleService.getCorrelation(page, null));
        return "role/list";
    }

    /**
     * 添加系统角色
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @GetMapping("/role/create")
    public String createSystemUserRole(Model model) {
        List<UserRole> userRoles = userRoleService.list();
        Set<UserPrivilege> userPrivileges = userPrivilegeService.getForSub();
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("userPrivileges", userPrivileges);
        return "role/edit";
    }

    /**
     * 编辑系统角色
     *
     * @param model 前台模型
     * @param id    角色编号
     * @return 视图页面
     */
    @GetMapping("/role/edit/{id}")
    public String editSystemUserRole(Model model, @PathVariable Long id) {
        List<UserRole> userRoles = userRoleService.list();
        UserRole userRole = userRoleService.getCorrelation(id);
        Set<UserPrivilege> userPrivileges = userPrivilegeService.getForSub();
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("userRole", userRole);
        model.addAttribute("userPrivileges", userPrivileges);
        return "role/edit";
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
    @RequestMapping(value = "/role", method = {RequestMethod.POST, RequestMethod.PUT})
    public String systemUserRoleCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserRole userRole, @RequestParam Long[] privilegeIds, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "role/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            if (!userRoleService.save(userRole) && !userRolePrivilegeService.save(userRole, privilegeIds)) {
                throw new BusinessException("添加角色失败！");
            }
        } else {
            // 修改
            UserRolePrivilege oldUserRolePrivilege = new UserRolePrivilege();
            oldUserRolePrivilege.setRoleId(userRole.getId());
            userRolePrivilegeService.remove(oldUserRolePrivilege);
            if (!userRoleService.updateById(userRole) && !userRolePrivilegeService.save(userRole, privilegeIds)) {
                throw new BusinessException("修改角色失败！");
            }
        }
        return "redirect:/system/user_role/role";
    }

    /**
     * 使用AJAX技术通过系统用户角色编号删除系统用户角色
     *
     * @param session session对象
     * @param id      对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/role/{id}")
    @ResponseBody
    public Map<String, Object> deleteSystemUserRoleForAjax(HttpSession session, @PathVariable Long id) {
        getSessionRoleId(session);
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (userRoleService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除用户角色成功！");
            return jsonMap;
        }
        throw new JsonException("删除用户角色失败！");
    }

    /**
     * 系统权限列表
     *
     * @param model 前台模型对象
     * @param page  分页页码
     * @return 视图页面
     */
    @GetMapping({"/privilege", "/privilege/{page}"})
    public String systemUserPrivilegeList(Model model, @PathVariable(required = false) Integer page) {
        page = page == null ? 1 : page;
        Page<UserPrivilege> userPrivilegePage = new Page<>(page, 10);
        model.addAttribute("userPrivileges", userPrivilegeService.page(userPrivilegePage, null));
        return "privilege/list";
    }

    /**
     * 添加系统权限
     *
     * @param model 前台模型对象
     * @return 视图页面
     */
    @GetMapping("/privilege/create")
    public String createSystemUserPrivilege(Model model) {
        List<UserPrivilege> userPrivileges = userPrivilegeService.getAndHandler();
        model.addAttribute("userPrivileges", userPrivileges);
        return "privilege/edit";
    }

    /**
     * 编辑系统权限
     *
     * @param model 前台模型对象
     * @param id    权限编号
     * @return 视图页面
     */
    @GetMapping("/privilege/edit/{id}")
    public String editSystemUserPrivilege(Model model, @PathVariable Long id) {
        List<UserPrivilege> userPrivileges = userPrivilegeService.list();
        UserPrivilege userPrivilege = userPrivilegeService.getById(id);
        model.addAttribute("userPrivileges", userPrivileges);
        model.addAttribute("userPrivilege", userPrivilege);
        return "privilege/edit";
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
    @RequestMapping(value = "/privilege", method = {RequestMethod.POST, RequestMethod.PUT})
    public String systemUserPrivilegeCreateOrEditHandle(HttpServletRequest request, Model model, @Validated UserPrivilege userPrivilege, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "privilege/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            if (!userPrivilegeService.save(userPrivilege)) {
                throw new BusinessException("添加权限失败！");
            }
        } else {
            // 修改
            if (!userPrivilegeService.updateById(userPrivilege)) {
                throw new BusinessException("修改权限失败！");
            }
        }
        return "redirect:/system/user_role/privilege";
    }

    /**
     * 使用AJAX技术通过系统用户权限编号删除系统用户权限
     *
     * @param id 系统用户权限编号
     * @return Ajax消息
     */
    @DeleteMapping("/privilege/{id}")
    @ResponseBody
    public Map<String, Object> deleteSystemUserPrivilegeForAjax(@PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (userPrivilegeService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除用户权限成功！");
            return jsonMap;
        }
        throw new JsonException("删除用户权限失败！");
    }

    /**
     * 通过AJAX技术获取系统用户角色拥有的权限
     *
     * @param roleId 系统用户角色编号
     * @return Ajax消息
     */
    @GetMapping("/privilege/load/{roleId}")
    @ResponseBody
    public Map<String, Object> getPrivilegesByRoleIdForAjax(@PathVariable Long roleId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        jsonMap.put("state", 1);
        jsonMap.put("privileges", userPrivilegeService.getByRoleId(roleId));
        return jsonMap;
    }
}
