package com.github.phonenumbermanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

    /**
     * 系统用户列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据
     * @return 系统用户列表JSON
     */
    @GetMapping("/user")
    @ApiOperation("系统用户列表")
    public R systemUserList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        getRoleId();
        return Objects.requireNonNull(R.ok().put("systemAdministratorId", systemAdministratorId)).put("systemUsers",
            systemUserService.getCorrelation(page, limit));
    }

    /**
     * 系统用户锁定与解锁
     *
     * @param id
     *            系统用户编号
     * @param locked
     *            锁定与解锁的标记
     * @return 是否成功
     */
    @PostMapping("/user/lock")
    @ApiOperation("系统用户锁定与解锁")
    public R systemUserLocked(@ApiParam(name = "系统用户编号", required = true) Long id,
        @ApiParam(name = "锁定与解锁的标记", required = true) Boolean locked) {
        getRoleId();
        SystemUser systemUser = new SystemUser();
        systemUser.setId(id);
        systemUser.setIsLocked(locked);
        if (!id.equals(systemAdministratorId)) {
            systemUserService.updateById(systemUser);
            SystemUser user = systemUserService.getById(id);
            return R.ok().put("isAccountNonLocked", user.isAccountNonLocked());
        }
        throw new JsonException();
    }

    /**
     * 通过系统用户编号查找
     *
     * @param id
     *            要查找的对应编号
     * @return 系统用户信息JSON
     */
    @GetMapping("/user/{id}")
    @ApiOperation("通过系统用户编号查找")
    public R getSystemUser(@ApiParam(name = "要查找的对应编号", required = true) @PathVariable Long id) {
        getRoleId();
        if (id == null) {
            id = systemUser.getId();
        }
        return Objects.requireNonNull(Objects
            .requireNonNull(Objects.requireNonNull(R.ok().put("communityCompanyType", communityCompanyType))
                .put("subdistrictCompanyType", subdistrictCompanyType))
            .put("systemAdministratorId", systemAdministratorId)).put("user", systemUserService.getCorrelation(id));
    }

    /**
     * 添加处理系统用户
     *
     * @param systemUser
     *            系统用户对象
     * @return 是否成功JSON
     */
    @PostMapping("/user")
    @ApiOperation("添加处理系统用户")
    public R systemUserCreateHandle(@ApiParam(name = "系统用户对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) SystemUser systemUser) {
        if (!systemUserService.save(systemUser)) {
            throw new JsonException("添加用户失败！");
        }
        return R.ok();
    }

    /**
     * 修改处理系统用户
     *
     * @param systemUser
     *            系统用户对象
     * @return 是否成功JSON
     */
    @PutMapping("/user")
    @ApiOperation("修改处理系统用户")
    public R systemUserModifyHandle(@ApiParam(name = "系统用户对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemUser systemUser) {
        if (!systemUserService.updateById(systemUser)) {
            throw new JsonException("修改用户失败！");
        }
        return R.ok();
    }

    /**
     * 通过系统用户编号删除系统用户
     *
     * @param id
     *            对应编号
     * @return 是否成功
     */
    @DeleteMapping("/user/{id}")
    @ApiOperation("通过系统用户编号删除系统用户")
    public R deleteSystemUser(@ApiParam(name = "要删除的用户名编号", required = true) @PathVariable Long id) {
        getRoleId();
        if (id.equals(systemAdministratorId)) {
            throw new JsonException("不允许删除超级管理员！");
        }
        if (systemUserService.removeCorrelationById(id)) {
            throw new JsonException("删除系统用户失败！");
        }
        return R.ok();
    }

    /**
     * 系统角色列表
     *
     * @param page
     *            分页页数
     * @param limit
     *            每页数据
     * @return 系统角色列表JSON
     */
    @GetMapping("/role")
    public R systemUserRoleList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        if (page == null) {
            return R.ok().put("userRoles", userRoleService.getCorrelation(page, limit));
        }
        return R.ok().put("userRoles", userRoleService.list());
    }

    /**
     * 通过系统角色编号查找
     *
     * @param id
     *            角色编号
     * @return 视图页面
     */
    @GetMapping("/role/{id}")
    @ApiOperation("通过系统角色编号查找")
    public R getSystemUserRole(@ApiParam(name = "角色编号", required = true) @PathVariable Long id) {
        return R.ok().put("userRole", userRoleService.getCorrelation(id));
    }

    /**
     * 添加处理系统角色
     *
     * @param userRole
     *            系统角色对象
     * @param privilegeIds
     *            系统权限编号数组
     * @return 视图页面
     */
    @PostMapping("/role")
    @ApiOperation("添加处理系统角色")
    public R systemUserRoleCreateHandle(
        @ApiParam(name = "系统角色对象", required = true) @RequestBody @Validated(CreateInputGroup.class) UserRole userRole,
        @ApiParam(name = "系统权限编号数组", required = true) Long[] privilegeIds) {
        if (!userRoleService.save(userRole)
            && !userRolePrivilegeService.saveBatch(getUserRolePrivileges(userRole.getId(), privilegeIds))) {
            throw new JsonException("添加角色失败！");
        }
        return R.ok();
    }

    /**
     * 修改处理系统角色
     *
     * @param userRole
     *            系统角色对象
     * @param privilegeIds
     *            系统权限编号数组
     * @return 视图页面
     */
    @PutMapping("/role")
    @ApiOperation("修改处理系统角色")
    public R systemUserRoleModifyHandle(
        @ApiParam(name = "系统角色对象", required = true) @RequestBody @Validated(ModifyInputGroup.class) UserRole userRole,
        @ApiParam(name = "系统权限编号数组", required = true) Long[] privilegeIds) {
        if (!userRoleService.updateById(userRole)
            && !userRolePrivilegeService.saveOrUpdateBatch(getUserRolePrivileges(userRole.getId(), privilegeIds))) {
            throw new JsonException("修改角色失败！");
        }
        return R.ok();
    }

    /**
     * 通过系统用户角色编号删除系统用户角色
     *
     * @param id
     *            对应编号
     * @return 是否删除成功
     */
    @DeleteMapping("/role/{id}")
    @ApiOperation("通过系统用户角色编号删除系统用户角色")
    public R deleteSystemUserRoleForAjax(@ApiParam(name = "需要删除的用户角色的编号", required = true) @PathVariable Long id) {
        getRoleId();
        QueryWrapper<RolePrivilegeRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        if (!userRoleService.removeCorrelationById(id)) {
            throw new JsonException("删除用户角色失败！");
        }
        return R.ok();
    }

    /**
     * 系统权限列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据
     * @return 系统权限列表JSON
     */
    @GetMapping("/privilege")
    @ApiOperation("系统权限列表")
    public R systemUserPrivilegeList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        if (page == null) {
            return R.ok().put("userPrivileges", userPrivilegeService.list());
        }
        Page<UserPrivilege> userPrivilegePage = new Page<>(page, limit);
        return R.ok().put("userPrivileges", userPrivilegeService.page(userPrivilegePage, null));
    }

    /**
     * 获取系统权限
     *
     * @param id
     *            权限编号
     * @return 视图页面
     */
    @GetMapping("/privilege/{id}")
    @ApiOperation("获取系统权限")
    public R getSystemUserPrivilege(@ApiParam(name = "权限编号", required = true) @PathVariable Long id) {
        return R.ok().put("userPrivileges", userPrivilegeService.getById(id));
    }

    /**
     * 添加处理系统权限
     *
     * @param userPrivilege
     *            系统用户权限对象
     * @return 视图页面
     */
    @PostMapping("/privilege")
    @ApiOperation("添加处理系统权限")
    public R systemUserPrivilegeCreateHandle(@ApiParam(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) UserPrivilege userPrivilege) {
        if (!userPrivilegeService.save(userPrivilege)) {
            throw new JsonException("添加权限失败！");
        }
        return R.ok();
    }

    /**
     * 修改处理系统权限
     *
     * @param userPrivilege
     *            系统用户权限对象
     * @return 视图页面
     */
    @PutMapping("/privilege")
    @ApiOperation("修改处理系统权限")
    public R systemUserPrivilegeModifyHandle(@ApiParam(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) UserPrivilege userPrivilege) {
        if (!userPrivilegeService.updateById(userPrivilege)) {
            throw new JsonException("修改权限失败！");
        }
        return R.ok();
    }

    /**
     * 通过系统用户权限编号删除系统用户权限
     *
     * @param id
     *            系统用户权限编号
     * @return 是否成功
     */
    @DeleteMapping("/privilege/{id}")
    @ApiOperation("通过系统用户权限编号删除系统用户权限")
    public R deleteSystemUserPrivilege(@ApiParam(name = "系统用户权限编号", required = true) @PathVariable Long id) {
        if (!userPrivilegeService.removeCorrelationById(id)) {
            throw new JsonException("删除用户权限失败！");
        }
        return R.ok();
    }

    /**
     * 获取系统用户角色拥有的权限
     *
     * @param roleId
     *            系统用户角色编号
     * @return 用户权限对象集合
     */
    @GetMapping("/privilege/role/{roleId}")
    @ApiOperation("获取系统用户角色拥有的权限")
    public R getPrivilegesByRoleId(@ApiParam(name = "系统用户角色编号", required = true) @PathVariable Long roleId) {
        return R.ok().put("userPrivileges", userPrivilegeService.getByRoleId(roleId));
    }

    /**
     * 获取新的用户角色与权限对象
     *
     * @param userRoleId
     *            用户角色编号
     * @param privilegeIds
     *            用户权限编号数组
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
