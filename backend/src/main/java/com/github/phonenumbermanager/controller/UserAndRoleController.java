package com.github.phonenumbermanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import com.github.phonenumbermanager.service.UserRolePrivilegeService;
import com.github.phonenumbermanager.service.UserRoleService;
import com.github.phonenumbermanager.validator.SystemUserInputValidator;
import com.github.phonenumbermanager.validator.UserPrivilegeInputValidator;
import com.github.phonenumbermanager.validator.UserRoleInputValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 系统用户与用户角色控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/system/user_role")
@Api(tags = "系统用户与用户角色控制器")
public class UserAndRoleController extends BaseController {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserPrivilegeService userPrivilegeService;
    @Resource
    private UserRolePrivilegeService userRolePrivilegeService;
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
     * @param page  分页页码
     * @param limit 每页数据
     * @return 系统用户列表JSON
     */
    @GetMapping("/user")
    @ApiOperation("系统用户列表")
    public Map<String, Object> systemUserList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("systemAdministratorId", systemAdministratorId);
        jsonMap.put("systemUsers", systemUserService.getCorrelation(page, limit));
        return jsonMap;
    }

    /**
     * 系统用户锁定与解锁
     *
     * @param id     系统用户编号
     * @param locked 锁定与解锁的标记
     * @return 是否成功
     */
    @PostMapping("/user/lock")
    @ApiOperation("系统用户锁定与解锁")
    public Map<String, Object> systemUserLocked(@ApiParam(name = "系统用户编号", required = true) Long id, @ApiParam(name = "锁定与解锁的标记", required = true) Boolean locked) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        getRoleId();
        SystemUser systemUser = new SystemUser();
        systemUser.setId(id);
        systemUser.setIsLocked(locked);
        if (!id.equals(systemAdministratorId)) {
            systemUserService.updateById(systemUser);
            jsonMap.put("state", 1);
            SystemUser user = systemUserService.getById(id);
            jsonMap.put("isAccountNonLocked", user.isAccountNonLocked());
        } else {
            jsonMap.put("state", 0);
        }
        return jsonMap;
    }

    /**
     * 通过系统用户编号查找
     *
     * @param id 要查找的对应编号
     * @return 系统用户信息JSON
     */
    @GetMapping("/user/{id}")
    @ApiOperation("通过系统用户编号查找")
    public Map<String, Object> getSystemUser(@ApiParam(name = "要查找的对应编号", required = true) @PathVariable Long id) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(4);
        jsonMap.put("communityCompanyType", communityCompanyType);
        jsonMap.put("subdistrictCompanyType", subdistrictCompanyType);
        jsonMap.put("systemAdministratorId", systemAdministratorId);
        if (id == null) {
            id = systemUser.getId();
        }
        jsonMap.put("user", systemUserService.getCorrelation(id));
        return jsonMap;
    }

    /**
     * 添加与修改处理系统用户
     *
     * @param request       HTTP请求对象
     * @param systemUser    系统用户对象
     * @param bindingResult 错误信息对象
     * @return 是否成功JSON
     */
    @RequestMapping(value = "/user", method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加与修改处理系统用户")
    public Map<String, Object> systemUserAddOrEditHandle(HttpServletRequest request, @ApiParam(name = "系统用户对象", required = true) @RequestBody @Validated SystemUser systemUser, BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            getRoleId();
            // 输出错误信息
            jsonMap.put("communityCompanyType", communityCompanyType);
            jsonMap.put("subdistrictCompanyType", subdistrictCompanyType);
            jsonMap.put("systemAdministratorId", systemAdministratorId);
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            if (!systemUserService.save(systemUser)) {
                throw new JsonException("添加用户失败！");
            }
        } else {
            // 修改
            if (!systemUserService.updateById(systemUser)) {
                throw new JsonException("修改用户失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过系统用户编号删除系统用户
     *
     * @param id 对应编号
     * @return 是否成功
     */
    @DeleteMapping("/user/{id}")
    @ApiOperation("通过系统用户编号删除系统用户")
    public Map<String, Object> deleteSystemUser(@ApiParam(name = "要删除的用户名编号", required = true) @PathVariable Long id) {
        getRoleId();
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
     * 系统角色列表
     *
     * @param page  分页页数
     * @param limit 每页数据
     * @return 系统角色列表JSON
     */
    @GetMapping("/role")
    public Map<String, Object> systemUserRoleList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (page == null) {
            jsonMap.put("userRoles", userRoleService.getCorrelation(page, limit));
        } else {
            jsonMap.put("userRoles", userRoleService.list());
        }
        return jsonMap;
    }

    /**
     * 通过系统角色编号查找
     *
     * @param id 角色编号
     * @return 视图页面
     */
    @GetMapping("/role/{id}")
    @ApiOperation("通过系统角色编号查找")
    public UserRole getSystemUserRole(@ApiParam(name = "角色编号", required = true) @PathVariable Long id) {
        return userRoleService.getCorrelation(id);
    }

    /**
     * 添加与修改处理系统角色
     *
     * @param request       HTTP请求对象
     * @param userRole      系统角色对象
     * @param privilegeIds  系统权限编号数组
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/role", method = {RequestMethod.POST, RequestMethod.PUT})
    public Map<String, Object> systemUserRoleCreateOrEditHandle(HttpServletRequest request, @ApiParam(name = "系统角色对象", required = true) @RequestBody @Validated UserRole userRole, @ApiParam(name = "系统权限编号数组", required = true) Long[] privilegeIds, BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            if (userRoleService.save(userRole)) {
                userRolePrivilegeService.saveBatch(getUserRolePrivileges(userRole.getId(), privilegeIds));
            } else {
                throw new JsonException("添加角色失败！");
            }
        } else {
            // 修改
            if (userRoleService.updateById(userRole)) {
                userRolePrivilegeService.saveOrUpdateBatch(getUserRolePrivileges(userRole.getId(), privilegeIds));
            } else {
                throw new JsonException("修改角色失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }


    /**
     * 通过系统用户角色编号删除系统用户角色
     *
     * @param id 对应编号
     * @return 是否删除成功
     */
    @DeleteMapping("/role/{id}")
    @ApiOperation("通过系统用户角色编号删除系统用户角色")
    public Map<String, Object> deleteSystemUserRoleForAjax(@ApiParam(name = "需要删除的用户角色的编号", required = true) @PathVariable Long id) {
        getRoleId();
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
     * @param page  分页页码
     * @param limit 每页数据
     * @return 系统权限列表JSON
     */
    @GetMapping("/privilege")
    @ApiOperation("系统权限列表")
    public Map<String, Object> systemUserPrivilegeList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (page == null) {
            jsonMap.put("userPrivileges", userPrivilegeService.list());
        } else {
            Page<UserPrivilege> userPrivilegePage = new Page<>(page, limit);
            jsonMap.put("userPrivileges", userPrivilegeService.page(userPrivilegePage, null));
        }
        return jsonMap;
    }

    /**
     * 获取系统权限
     *
     * @param id 权限编号
     * @return 视图页面
     */
    @GetMapping("/privilege/{id}")
    @ApiOperation("获取系统权限")
    public UserPrivilege getSystemUserPrivilege(@ApiParam(name = "权限编号", required = true) @PathVariable Long id) {
        return userPrivilegeService.getById(id);
    }

    /**
     * 添加与修改处理系统权限
     *
     * @param request       HTTP请求对象
     * @param userPrivilege 系统用户权限对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/privilege", method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加与修改处理系统权限")
    public Map<String, Object> systemUserPrivilegeCreateOrEditHandle(HttpServletRequest request, @ApiParam(name = "系统用户权限对象", required = true) @RequestBody @Validated UserPrivilege userPrivilege, BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            // 添加
            if (!userPrivilegeService.save(userPrivilege)) {
                throw new JsonException("添加权限失败！");
            }
        } else {
            // 修改
            if (!userPrivilegeService.updateById(userPrivilege)) {
                throw new JsonException("修改权限失败！");
            }
        }
        return jsonMap;
    }

    /**
     * 通过系统用户权限编号删除系统用户权限
     *
     * @param id 系统用户权限编号
     * @return 是否成功
     */
    @DeleteMapping("/privilege/{id}")
    @ApiOperation("通过系统用户权限编号删除系统用户权限")
    public Map<String, Object> deleteSystemUserPrivilege(@ApiParam(name = "系统用户权限编号", required = true) @PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        if (userPrivilegeService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除用户权限成功！");
            return jsonMap;
        }
        throw new JsonException("删除用户权限失败！");
    }

    /**
     * 获取系统用户角色拥有的权限
     *
     * @param roleId 系统用户角色编号
     * @return 用户权限对象集合
     */
    @GetMapping("/privilege/role/{roleId}")
    @ApiOperation("获取系统用户角色拥有的权限")
    public Set<UserPrivilege> getPrivilegesByRoleId(@ApiParam(name = "系统用户角色编号", required = true) @PathVariable Long roleId) {
        return userPrivilegeService.getByRoleId(roleId);
    }

    /**
     * 获取新的用户角色与权限对象
     *
     * @param userRoleId   用户角色编号
     * @param privilegeIds 用户权限编号数组
     * @return 用户角色与权限对象
     */
    private List<RolePrivilegeRelation> getUserRolePrivileges(Long userRoleId, Long[] privilegeIds) {
        List<RolePrivilegeRelation> rolePrivilegeRelations = new ArrayList<>();
        for (Long privilegeId : privilegeIds) {
            RolePrivilegeRelation rolePrivilegeRelation = new RolePrivilegeRelation();
            rolePrivilegeRelation.setPrivilegeId(privilegeId).setRoleId(userRoleId);
            rolePrivilegeRelations.add(rolePrivilegeRelation);
        }
        return rolePrivilegeRelations;
    }
}
