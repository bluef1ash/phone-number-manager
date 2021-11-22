package com.github.phonenumbermanager.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 系统用户与系统权限控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/system")
@Api(tags = "系统用户与系统权限控制器")
public class UserAndPermissionController extends BaseController {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private SystemPermissionService systemPermissionService;

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
        getEnvironmentVariable();
        return R.ok().put("systemUsers", systemUserService.pageCorrelation(systemUser.getCompanies(), page, limit));
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
    @PostMapping("/user/lock/{id}")
    @ApiOperation("系统用户锁定与解锁")
    public R systemUserLocked(@ApiParam(name = "系统用户编号", required = true) @PathVariable Long id,
        @ApiParam(name = "锁定与解锁的标记", required = true) Boolean locked) {
        getEnvironmentVariable();
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
    public R getSystemUserById(@ApiParam(name = "要查找的对应编号", required = true) @PathVariable Long id) {
        getEnvironmentVariable();
        return R.ok().put("user", systemUserService.getCorrelation(id));
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
    public R removeSystemUser(@ApiParam(name = "要删除的用户名编号", required = true) @PathVariable Long id) {
        getEnvironmentVariable();
        if (id.equals(systemAdministratorId)) {
            throw new JsonException("不允许删除超级管理员！");
        }
        if (systemUserService.removeCorrelationById(id)) {
            throw new JsonException("删除系统用户失败！");
        }
        return R.ok();
    }

    /**
     * 通过单位编号加载系统用户
     *
     * @param companyId
     *            社区编号
     * @return 系统用户对象集合
     */
    @GetMapping("/user/company/{companyId}")
    @ApiOperation("通过单位编号加载系统用户")
    public R loadSystemUserByCompanyId(@ApiParam(name = "社区编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("systemUsers", systemUserService.listCorrelationByCompanyId(companyId));
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
    @GetMapping("/permission")
    @ApiOperation("系统权限列表")
    public R systemPermissionList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        getEnvironmentVariable();
        if (page == null) {
            return R.ok().put("systemPermissions", systemPermissionService.list());
        }
        return R.ok().put("systemPermissions", systemPermissionService.page(new Page<>(page, limit), null));
    }

    /**
     * 通过编号获取系统权限
     *
     * @param id
     *            权限编号
     * @return 视图页面
     */
    @GetMapping("/permission/{id}")
    @ApiOperation("通过编号获取系统权限")
    public R getSystemUserPrivilegeById(@ApiParam(name = "权限编号", required = true) @PathVariable Long id) {
        return R.ok().put("systemPermissions", systemPermissionService.getById(id));
    }

    /**
     * 添加处理系统权限
     *
     * @param systemPermission
     *            系统权限对象
     * @return 视图页面
     */
    @PostMapping("/permission")
    @ApiOperation("添加处理系统权限")
    public R systemPermissionCreateHandle(@ApiParam(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) SystemPermission systemPermission) {
        if (!systemPermissionService.save(systemPermission)) {
            throw new JsonException("添加权限失败！");
        }
        return R.ok();
    }

    /**
     * 修改处理系统权限
     *
     * @param systemPermission
     *            系统权限对象
     * @return 视图页面
     */
    @PutMapping("/permission")
    @ApiOperation("修改处理系统权限")
    public R systemPermissionModifyHandle(@ApiParam(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemPermission systemPermission) {
        if (!systemPermissionService.updateById(systemPermission)) {
            throw new JsonException("修改权限失败！");
        }
        return R.ok();
    }

    /**
     * 通过系统权限编号删除系统权限
     *
     * @param id
     *            系统权限编号
     * @return 是否成功
     */
    @DeleteMapping("/permission/{id}")
    @ApiOperation("通过系统用户权限编号删除系统用户权限")
    public R removeSystemPermission(@ApiParam(name = "系统用户权限编号", required = true) @PathVariable Long id) {
        if (!systemPermissionService.removeCorrelationById(id)) {
            throw new JsonException("删除用户权限失败！");
        }
        return R.ok();
    }

    /**
     * 获取系统用户单位拥有的权限
     *
     * @param companyId
     *            单位编号
     * @return 用户权限对象集合
     */
    @GetMapping("/permission/company/{companyId}")
    @ApiOperation("获取系统用户单位拥有的权限")
    public R getPermissionsByCompanyId(@ApiParam(name = "单位编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("systemPermissions", systemPermissionService.listByCompanyId(companyId));
    }
}
