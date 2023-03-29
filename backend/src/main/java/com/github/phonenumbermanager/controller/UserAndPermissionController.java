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
import com.github.phonenumbermanager.vo.BatchRestfulVO;
import com.github.phonenumbermanager.vo.CompanyVO;
import com.github.phonenumbermanager.vo.SystemUserLoginVO;
import com.github.phonenumbermanager.vo.SystemUserVO;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 系统用户与系统权限控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@Tag(name = "系统用户与系统权限控制器")
public class UserAndPermissionController extends BaseController {
    private final ConfigurationService configurationService;
    private final SystemUserService systemUserService;
    private final SystemPermissionService systemPermissionService;
    private final RedisUtil redisUtil;

    /**
     * 用户登录
     *
     * @param request
     *            HTTP 请求对象
     * @param systemUserLoginVo
     *            系统用户登录对象
     * @return JWT 令牌和当前系统用户信息
     * @throws LoginException
     *             登录异常
     */
    @PostMapping("/account/login")
    @Operation(summary = "用户登录")
    public R login(HttpServletRequest request,
        @Parameter(name = "系统用户登录对象", required = true) @RequestBody SystemUserLoginVO systemUserLoginVo)
        throws LoginException {
        String captchaCodeCacheKey =
            SystemConstant.CAPTCHA_ID_KEY + SystemConstant.REDIS_EXPLODE + systemUserLoginVo.getCaptchaId();
        String captchaCode = (String)redisUtil.get(captchaCodeCacheKey);
        if (captchaCode == null || !captchaCode.equals(systemUserLoginVo.getCaptcha())) {
            redisUtil.delete(captchaCodeCacheKey);
            throw new LoginException("登录图形验证码输入错误！");
        }
        Authentication authentication = systemUserService.authentication(systemUserLoginVo.getUsername(),
            systemUserLoginVo.getPassword(), JakartaServletUtil.getClientIP(request));
        SystemUser systemUser = (SystemUser)authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(SystemConstant.SYSTEM_USER_ID_KEY, systemUser.getId());
        claims.put(SystemConstant.CLAIM_KEY_CREATED, LocalDateTime.now());
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("token",
            JWTUtil.createToken(claims, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8)));
        jsonMap.put("currentUser", convertSystemUserVO(systemUser));
        redisUtil.setEx(SystemConstant.SYSTEM_USER_ID_KEY + SystemConstant.REDIS_EXPLODE + systemUser.getId(),
            JSONUtil.toJsonStr(systemUser), 7, TimeUnit.DAYS);
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
    @Operation(summary = "生成图案验证码数据")
    public void captcha(HttpServletResponse response, @Parameter(name = "随机 UUID", required = true) String code)
        throws IOException {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 40, 4, RandomUtil.randomInt(20, 30));
        redisUtil.setEx(SystemConstant.CAPTCHA_ID_KEY + SystemConstant.REDIS_EXPLODE + code, captcha.getCode(), 5,
            TimeUnit.MINUTES);
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
    @Operation(summary = "退出登录")
    public R logout(@Parameter(name = "系统用户编号") Long id) {
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
    @Operation(summary = "获取当前登录用户信息")
    public R getCurrentSystemUser() {
        SystemUserVO systemUserVO = new SystemUserVO();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            systemUserVO = convertSystemUserVO((SystemUser)authentication.getPrincipal());
        }
        return R.ok().put("data", systemUserVO);
    }

    /**
     * 获取系统用户分页列表
     *
     * @param request
     *            HTTP 请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统用户分页列表
     */
    @GetMapping("/system/user")
    @Operation(summary = "获取系统用户分页列表")
    public R systemUserList(HttpServletRequest request, @Parameter(name = "分页页码") Integer current,
        @Parameter(name = "每页数据") Integer pageSize) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        getSearchParameter(request);
        return R.ok().put("data",
            systemUserService.pageCorrelation(currentSystemUser.getCompanies(), current, pageSize, search, sort));
    }

    /**
     * 获取系统用户表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 系统用户表单列表
     */
    @GetMapping("/system/user/select-list")
    @Operation(summary = "获取系统用户表单列表")
    public R systemUserSelectList(Long[] parentIds) {
        return R.ok().put("data", systemUserService.treeSelectList(parentIds));
    }

    /**
     * 单独字段修改系统用户
     *
     * @param id
     *            系统用户编号
     * @param systemUser
     *            系统用户对象
     * @return 修改成功或者失败
     */
    @PatchMapping("/system/user/{id}")
    @Operation(summary = "单独字段修改系统用户")
    public R systemUserModifyHandlePatch(HttpServletResponse response,
        @Parameter(name = "系统用户编号", required = true) @PathVariable Long id,
        @Parameter(name = "系统用户对象", required = true) @RequestBody SystemUser systemUser) {
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
        throw new JsonException("单独字段修改系统用户失败！");
    }

    /**
     * 通过系统用户编号查找系统用户详细信息
     *
     * @param id
     *            要查找的系统用户编号
     * @return 系统用户详细信息
     */
    @GetMapping("/system/user/{id}")
    @Operation(summary = "通过系统用户编号查找系统用户详细信息")
    public R getSystemUserById(@Parameter(name = "要查找的系统用户编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", systemUserService.getCorrelation(id));
    }

    /**
     * 添加处理系统用户
     *
     * @param systemUser
     *            系统用户对象
     * @return 添加成功或者失败
     */
    @PostMapping("/system/user")
    @Operation(summary = "添加处理系统用户")
    public R systemUserCreateHandle(@Parameter(name = "系统用户对象",
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
     * @return 修改成功或者失败
     */
    @PutMapping("/system/user/{id}")
    @Operation(summary = "修改处理系统用户")
    public R systemUserModifyHandle(@Parameter(name = "要修改的用户名编号", required = true) @PathVariable Long id,
        @Parameter(name = "系统用户对象",
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
        throw new JsonException("修改系统用户失败！");
    }

    /**
     * 通过系统用户编号删除系统用户
     *
     * @param id
     *            要删除的系统用户编号
     * @return 删除成功或者失败
     */
    @DeleteMapping("/system/user/{id}")
    @Operation(summary = "通过系统用户编号删除系统用户")
    public R removeSystemUser(@Parameter(name = "要删除的系统用户编号", required = true) @PathVariable Long id) {
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
    @Operation(summary = "通过单位编号加载系统用户")
    public R loadSystemUserByCompanyId(@Parameter(name = "社区编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("data", systemUserService.listCorrelationByCompanyId(companyId));
    }

    /**
     * 增删改批量操作系统用户
     *
     * @param batchRestfulVO
     *            批量操作视图对象
     * @return 批量操作成功或者失败
     */
    @PostMapping("/system/user/batch")
    @Operation(summary = "增删改批量操作系统用户")
    public R systemUserBatch(
        @Parameter(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVO batchRestfulVO) {
        if (batchRestfulVO.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVO.getData(), Long.class);
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
     * 获取系统权限分页列表
     *
     * @param request
     *            HTTP响应对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统权限分页列表
     */
    @GetMapping("/system/permission")
    @Operation(summary = "获取系统权限分页列表")
    public R systemPermissionList(HttpServletRequest request, @Parameter(name = "分页页码") Integer current,
        @Parameter(name = "每页数据") Integer pageSize) {
        QueryWrapper<SystemPermission> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", systemPermissionService.treePage(current, pageSize, wrapper));
    }

    /**
     * 获取系统权限表单列表
     *
     * @param parentIds
     *            查找的父级编号数组
     * @return 系统权限表单列表
     */
    @GetMapping("/system/permission/select-list")
    @Operation(summary = "获取系统权限表单列表")
    public R systemPermissionSelectList(Long[] parentIds) {
        return R.ok().put("data", systemPermissionService.treeSelectList(parentIds));
    }

    /**
     * 通过编号获取系统权限的详细信息
     *
     * @param id
     *            权限编号
     * @return 单个系统权限的详细信息
     */
    @GetMapping("/system/permission/{id}")
    @Operation(summary = "通过编号获取系统权限的详细信息")
    public R getSystemUserPrivilegeById(@Parameter(name = "权限编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", systemPermissionService.getById(id));
    }

    /**
     * 添加处理系统权限
     *
     * @param systemPermission
     *            系统权限对象
     * @return 添加成功或者失败
     */
    @PostMapping("/system/permission")
    @Operation(summary = "添加处理系统权限")
    public R systemPermissionCreateHandle(@Parameter(name = "系统用户权限对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) SystemPermission systemPermission) {
        if (systemPermissionService.save(systemPermission)) {
            return R.ok();
        }
        throw new JsonException("添加系统权限失败！");
    }

    /**
     * 修改处理系统权限
     *
     * @param id
     *            要修改的系统权限编号
     * @param systemPermission
     *            系统权限对象
     * @return 修改成功或者失败
     */
    @PutMapping("/system/permission/{id}")
    @Operation(summary = "修改处理系统权限")
    public R systemPermissionModifyHandle(@Parameter(name = "要修改的系统权限编号", required = true) @PathVariable Long id,
        @Parameter(name = "系统用户权限对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemPermission systemPermission) {
        systemPermission.setId(id).setVersion(systemPermissionService
            .getOne(new LambdaQueryWrapper<SystemPermission>().eq(SystemPermission::getId, id)).getVersion());
        if (systemPermissionService.updateById(systemPermission)) {
            return R.ok();
        }
        throw new JsonException("修改修改权限失败！");
    }

    /**
     * 通过系统权限编号删除系统权限
     *
     * @param id
     *            系统权限编号
     * @return 是否成功
     */
    @DeleteMapping("/system/permission/{id}")
    @Operation(summary = "通过系统用户权限编号删除系统用户权限")
    public R removeSystemPermission(@Parameter(name = "系统用户权限编号", required = true) @PathVariable Long id) {
        if (systemPermissionService
            .count(new LambdaQueryWrapper<SystemPermission>().eq(SystemPermission::getParentId, id)) > 0) {
            throw new JsonException("不允许删除有子权限的系统权限！");
        }
        if (!systemPermissionService.removeById(id)) {
            throw new JsonException("删除系统用户权限失败！");
        }
        return R.ok();
    }

    /**
     * 增删改批量操作系统权限
     *
     * @param batchRestfulVO
     *            批量操作视图对象
     * @return 批量操作成功或者失败
     */
    @PostMapping("/system/permission/batch")
    @Operation(summary = "增删改批量操作系统权限")
    public R systemPermissionBatch(
        @Parameter(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVO batchRestfulVO) {
        if (batchRestfulVO.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVO.getData(), Long.class);
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
     * @return 系统用户权限对象集合
     */
    @GetMapping("/system/permission/company/{companyId}")
    @Operation(summary = "获取系统用户单位拥有的权限")
    public R getPermissionsByCompanyId(@Parameter(name = "单位编号", required = true) @PathVariable Long companyId) {
        return R.ok().put("data", systemPermissionService.listByCompanyId(companyId));
    }

    /**
     * 转换系统用户信息对象
     *
     * @param systemUser
     *            当前系统用户对象
     * @return 系统用户信息视图对象
     */
    private SystemUserVO convertSystemUserVO(SystemUser systemUser) {
        SystemUserVO systemUserVO = new SystemUserVO();
        BeanUtil.copyProperties(systemUser, systemUserVO);
        if (systemUser.getCompanies() != null) {
            List<CompanyVO> companyVOs = systemUser.getCompanies().parallelStream().map(company -> {
                CompanyVO companyVO = new CompanyVO();
                BeanUtil.copyProperties(company, companyVO);
                return companyVO;
            }).toList();
            systemUserVO.setCompanyVOs(companyVOs);
        }
        return systemUserVO;
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
            redisUtil.set(SystemConstant.SYSTEM_USER_ID_KEY + SystemConstant.REDIS_EXPLODE + id,
                JSONUtil.toJsonStr(user));
        }
    }
}
