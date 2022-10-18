package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;
import com.github.phonenumbermanager.vo.CompanyVo;
import com.github.phonenumbermanager.vo.SystemUserLoginVo;
import com.github.phonenumbermanager.vo.SystemUserVo;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 系统用户与系统权限控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@Api(tags = "系统用户与系统权限控制器")
public class UserAndPermissionController extends BaseController {
    private final ConfigurationService configurationService;
    private final SystemUserService systemUserService;
    private final SystemPermissionService systemPermissionService;
    private final RedisUtil redisUtil;

    /**
     * 用户登录
     *
     * @param request
     *            HTTP请求对象
     * @param systemUserLoginVo
     *            系统用户登录对象
     * @return JSON对象
     * @throws LoginException
     *             登录异常
     */
    @PostMapping("/account/login")
    @ApiOperation("用户登录")
    public R login(HttpServletRequest request,
        @ApiParam(name = "系统用户登录对象", required = true) @RequestBody SystemUserLoginVo systemUserLoginVo)
        throws LoginException {
        String captchaCodeCacheKey = SystemConstant.CAPTCHA_ID_KEY + "::" + systemUserLoginVo.getCaptchaId();
        String captchaCode = (String)redisUtil.get(captchaCodeCacheKey);
        if (captchaCode == null || !captchaCode.equals(systemUserLoginVo.getCaptcha())) {
            redisUtil.delete(captchaCodeCacheKey);
            throw new LoginException("登录图形验证码输入错误！");
        }
        Authentication authentication = systemUserService.authentication(systemUserLoginVo.getUsername(),
            systemUserLoginVo.getPassword(), ServletUtil.getClientIP(request));
        SystemUser systemUser = (SystemUser)authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(SystemConstant.SYSTEM_USER_ID_KEY, systemUser.getId());
        claims.put(SystemConstant.CLAIM_KEY_CREATED, LocalDateTime.now());
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("token",
            JWTUtil.createToken(claims, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8)));
        jsonMap.put("currentUser", getSystemUserVo(systemUser));
        redisUtil.setEx(SystemConstant.SYSTEM_USER_ID_KEY + "::" + systemUser.getId(), JSONUtil.toJsonStr(systemUser),
            7, TimeUnit.DAYS);
        redisUtil.delete(captchaCodeCacheKey);
        return R.ok(jsonMap);
    }

    /**
     * 生成图案验证码数据
     *
     * @param response
     *            HTTP响应对象
     * @param code
     *            随机 UUID
     * @throws IOException
     *             IO异常
     */
    @GetMapping("/account/captcha")
    @ApiOperation("生成图案验证码数据")
    public void captcha(HttpServletResponse response, @ApiParam(name = "随机 UUID", required = true) String code)
        throws IOException {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 40, 4, RandomUtil.randomInt(20, 30));
        redisUtil.setEx(SystemConstant.CAPTCHA_ID_KEY + "::" + code, captcha.getCode(), 5, TimeUnit.MINUTES);
        captcha.write(response.getOutputStream());
    }

    /**
     * 退出登录
     *
     * @return 是否成功
     */
    @SuppressWarnings("all")
    @CacheEvict(cacheNames = {SystemConstant.SYSTEM_USER_ID_KEY, SystemConstant.SYSTEM_MENU_KEY}, key = "#id")
    @PostMapping("/account/logout")
    @ApiOperation("退出登录")
    public R logout(@ApiParam("系统用户编号") Long id) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SecurityContextHolder.clearContext();
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    @GetMapping("/system/user/current")
    @ApiOperation("获取当前登录用户信息")
    public R getCurrentSystemUser() {
        SystemUserVo systemUserVo = new SystemUserVo();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            systemUserVo = getSystemUserVo((SystemUser)authentication.getPrincipal());
        }
        return R.ok().put("data", systemUserVo);
    }

    /**
     * 系统用户列表
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统用户列表JSON
     */
    @GetMapping("/system/user")
    @ApiOperation("系统用户列表")
    public R systemUserList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据") Integer pageSize) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        getSearchParameter(request);
        return R.ok().put("data",
            systemUserService.pageCorrelation(currentSystemUser.getCompanies(), current, pageSize, search, sort));
    }

    /**
     * 系统用户表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 系统用户表单列表JSON
     */
    @GetMapping("/system/user/select-list")
    @ApiOperation("系统用户表单列表")
    public R systemUserSelectList(Long[] parentIds) {
        return R.ok().put("data", systemUserService.treeSelectList(parentIds));
    }

    /**
     * 单独字段修改系统用户
     *
     * @param id
     *            系统用户编号
     * @param systemUser
     *            * 系统用户对象
     * @return 是否成功
     */
    @PatchMapping("/system/user/{id}")
    @ApiOperation("单独字段修改系统用户")
    public R systemUserModifyHandlePatch(HttpServletResponse response,
        @ApiParam(name = "系统用户编号", required = true) @PathVariable Long id,
        @ApiParam(name = "系统用户对象", required = true) @RequestBody SystemUser systemUser) {
        Map<String, JSONObject> configurationMap = configurationService.mapAll();
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        if (id.equals(systemAdministratorId)) {
            if (systemUser.getIsEnabled() != null && !systemUser.isEnabled()) {
                return R.error(response, ExceptionCode.NOT_MODIFIED.getCode(), "不能禁用系统管理员！");
            }
            if (systemUser.getIsLocked() != null && !systemUser.isAccountNonLocked()) {
                return R.error(response, ExceptionCode.NOT_MODIFIED.getCode(), "不能锁定系统管理员！");
            }
        }
        systemUser.setId(id).setVersion(
            systemUserService.getOne(new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getId, id)).getVersion());
        if (systemUserService.updateById(systemUser)) {
            flushCurrentUser(id);
            return R.ok();
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
    @GetMapping("/system/user/{id}")
    @ApiOperation("通过系统用户编号查找")
    public R getSystemUserById(@ApiParam(name = "要查找的对应编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", systemUserService.getCorrelation(id));
    }

    /**
     * 添加处理系统用户
     *
     * @param systemUser
     *            系统用户对象
     * @return 是否成功JSON
     */
    @PostMapping("/system/user")
    @ApiOperation("添加处理系统用户")
    public R systemUserCreateHandle(@ApiParam(name = "系统用户对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) SystemUser systemUser) {
        if (systemUserService.save(systemUser)) {
            return R.ok();
        }
        throw new JsonException("添加用户失败！");
    }

    /**
     * 修改处理系统用户
     *
     * @param systemUser
     *            系统用户对象
     * @return 是否成功JSON
     */
    @PutMapping("/system/user/{id}")
    @ApiOperation("修改处理系统用户")
    public R systemUserModifyHandle(@ApiParam(name = "要修改的用户名编号", required = true) @PathVariable Long id,
        @ApiParam(name = "系统用户对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemUser systemUser) {
        Map<String, JSONObject> configurationMap = configurationService.mapAll();
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        if (!id.equals(systemAdministratorId)) {
            systemUser.setIsLocked(false).setIsEnabled(true);
        }
        systemUser.setId(id).setVersion(
            systemUserService.getOne(new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getId, id)).getVersion());
        if (systemUserService.updateById(systemUser)) {
            flushCurrentUser(id);
            return R.ok();
        }
        throw new JsonException("修改用户失败！");
    }

    /**
     * 通过系统用户编号删除系统用户
     *
     * @param id
     *            对应编号
     * @return 是否成功
     */
    @DeleteMapping("/system/user/{id}")
    @ApiOperation("通过系统用户编号删除系统用户")
    public R removeSystemUser(@ApiParam(name = "要删除的用户名编号", required = true) @PathVariable Long id) {
        Map<String, JSONObject> configurationMap = configurationService.mapAll();
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        if (id.equals(systemAdministratorId)) {
            throw new JsonException("不允许删除超级管理员！");
        }
        if (systemUserService.removeById(id)) {
            return R.ok();
        }
        throw new JsonException("删除系统用户失败！");
    }

    /**
     * 通过单位编号加载系统用户
     *
     * @param companyId
     *            社区编号
     * @return 系统用户对象集合
     */
    @GetMapping("/system/user/company/{companyId}")
    @ApiOperation("通过单位编号加载系统用户")
    public R loadSystemUserByCompanyId(@ApiParam(name = "社区编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("data", systemUserService.listCorrelationByCompanyId(companyId));
    }

    /**
     * 增删改批量操作系统用户
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/system/user/batch")
    @ApiOperation("增删改批量操作系统用户")
    public R systemUserBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            SystemUser currentSystemUser =
                (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ids = ids.stream().filter(id -> !id.equals(currentSystemUser.getId())).collect(Collectors.toList());
            if (systemUserService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 系统权限列表
     *
     * @param request
     *            HTTP响应对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统权限列表JSON
     */
    @GetMapping("/system/permission")
    @ApiOperation("系统权限列表")
    public R systemPermissionList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据") Integer pageSize) {
        QueryWrapper<SystemPermission> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", systemPermissionService.treePage(current, pageSize, wrapper));
    }

    /**
     * 系统权限表单列表
     *
     * @param parentIds
     *            查找的父级编号数组
     * @return 系统权限表单列表JSON
     */
    @GetMapping("/system/permission/select-list")
    @ApiOperation("系统权限表单列表")
    public R systemPermissionSelectList(Long[] parentIds) {
        return R.ok().put("data", systemPermissionService.treeSelectList(parentIds));
    }

    /**
     * 通过编号获取系统权限
     *
     * @param id
     *            权限编号
     * @return 视图页面
     */
    @GetMapping("/system/permission/{id}")
    @ApiOperation("通过编号获取系统权限")
    public R getSystemUserPrivilegeById(@ApiParam(name = "权限编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", systemPermissionService.getById(id));
    }

    /**
     * 添加处理系统权限
     *
     * @param systemPermission
     *            系统权限对象
     * @return 视图页面
     */
    @PostMapping("/system/permission")
    @ApiOperation("添加处理系统权限")
    public R systemPermissionCreateHandle(@ApiParam(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) SystemPermission systemPermission) {
        if (systemPermissionService.save(systemPermission)) {
            return R.ok();
        }
        throw new JsonException("添加权限失败！");
    }

    /**
     * 修改处理系统权限
     *
     * @param id
     *            要修改的系统权限编号
     * @param systemPermission
     *            系统权限对象
     * @return 视图页面
     */
    @PutMapping("/system/permission/{id}")
    @ApiOperation("修改处理系统权限")
    public R systemPermissionModifyHandle(@ApiParam(name = "要修改的系统权限编号", required = true) @PathVariable Long id,
        @ApiParam(name = "系统用户权限对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemPermission systemPermission) {
        systemPermission.setId(id).setVersion(systemPermissionService
            .getOne(new LambdaQueryWrapper<SystemPermission>().eq(SystemPermission::getId, id)).getVersion());
        if (systemPermissionService.updateById(systemPermission)) {
            return R.ok();
        }
        throw new JsonException("修改权限失败！");
    }

    /**
     * 通过系统权限编号删除系统权限
     *
     * @param id
     *            系统权限编号
     * @return 是否成功
     */
    @DeleteMapping("/system/permission/{id}")
    @ApiOperation("通过系统用户权限编号删除系统用户权限")
    public R removeSystemPermission(@ApiParam(name = "系统用户权限编号", required = true) @PathVariable Long id) {
        if (systemPermissionService
            .count(new LambdaQueryWrapper<SystemPermission>().eq(SystemPermission::getParentId, id)) > 0) {
            throw new JsonException("不允许删除有子权限的系统权限！");
        }
        if (!systemPermissionService.removeById(id)) {
            throw new JsonException("删除用户权限失败！");
        }
        return R.ok();
    }

    /**
     * 增删改批量操作系统权限
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/system/permission/batch")
    @ApiOperation("增删改批量操作系统权限")
    public R systemPermissionBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            if (systemPermissionService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 获取系统用户单位拥有的权限
     *
     * @param companyId
     *            单位编号
     * @return 用户权限对象集合
     */
    @GetMapping("/system/permission/company/{companyId}")
    @ApiOperation("获取系统用户单位拥有的权限")
    public R getPermissionsByCompanyId(@ApiParam(name = "单位编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("data", systemPermissionService.listByCompanyId(companyId));
    }

    /**
     * 获取系统用户信息
     *
     * @return 系统用户信息视图对象
     */
    private SystemUserVo getSystemUserVo(SystemUser systemUser) {
        SystemUserVo systemUserVo = new SystemUserVo();
        BeanUtil.copyProperties(systemUser, systemUserVo);
        if (systemUser.getCompanies() != null) {
            List<CompanyVo> companyVos = systemUser.getCompanies().parallelStream().map(company -> {
                CompanyVo companyVo = new CompanyVo();
                BeanUtil.copyProperties(company, companyVo);
                return companyVo;
            }).collect(Collectors.toList());
            systemUserVo.setCompanies(companyVos);
        }
        return systemUserVo;
    }

    /**
     * 刷新当前登录系统用户信息缓存
     *
     * @param id
     *            更改系统用户的编号
     */
    private void flushCurrentUser(Long id) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (id.equals(currentSystemUser.getId())) {
            SystemUser user = systemUserService.getCorrelation(id);
            redisUtil.set(SystemConstant.SYSTEM_USER_ID_KEY + "::" + id, JSONUtil.toJsonStr(user));
        }
    }
}
