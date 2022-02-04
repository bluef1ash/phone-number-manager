package com.github.phonenumbermanager.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.GeetestLibUtil;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 系统用户与系统权限控制器
 *
 * @author 廿二月的天
 */
@RestController
@Api(tags = "系统用户与系统权限控制器")
public class UserAndPermissionController extends BaseController {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private SystemPermissionService systemPermissionService;
    @Resource
    private CompanyService companyService;
    @Resource
    private RedisUtil redisUtil;

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
        getEnvironmentVariable();
        Authentication authentication = systemUserService.authentication(systemUserLoginVo.getPhoneNumber(),
            systemUserLoginVo.getPassword(), ServletUtil.getClientIP(request));
        SystemUser principal = (SystemUser)authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(SystemConstant.SYSTEM_USER_ID_KEY, principal.getId());
        claims.put(SystemConstant.CLAIM_KEY_CREATED, LocalDateTime.now());
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("token",
            JWTUtil.createToken(claims, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8)));
        jsonMap.put("systemUserInfo", getSystemUserVo(principal));
        return R.ok(jsonMap);
    }

    /**
     * 生成图案验证码数据
     *
     * @param request
     *            HTTP请求对象
     * @param browserType
     *            浏览器类型
     * @return 验证图案数据
     */
    @GetMapping("/account/recaptcha")
    @ApiOperation("生成图案验证码数据")
    public String captcha(HttpServletRequest request, @ApiParam(name = "浏览器类型") String browserType) {
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", browserType);
        param.put("ip_address", ServletUtil.getClientIP(request));
        int gtServerStatus = gtSdk.preProcess(param);
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        return gtSdk.getResponseStr();
    }

    /**
     * 退出登录
     *
     * @return 是否成功
     */
    @PostMapping("/account/logout")
    @ApiOperation("退出登录")
    public R logout() {
        getEnvironmentVariable();
        SecurityContextHolder.clearContext();
        redisUtil.delete(SystemConstant.SYSTEM_USER_ID_KEY + systemUser.getId());
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    @GetMapping("/system/current-user")
    @ApiOperation("获取当前登录用户信息")
    public R currentSystemUser() {
        getEnvironmentVariable();
        SystemUserVo systemUserVo = getSystemUserVo(systemUser);
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
        getEnvironmentVariable();
        List<Company> companies = systemUser.getCompanies();
        if (companies == null && systemUser.getId()
            .equals(Long.valueOf((String)configurationMap.get("system_administrator_id").get("content")))) {
            companies = companyService.list();
        }
        String params = request.getParameter("params");
        JSONObject search = null;
        JSONObject sort = null;
        if (!StrUtil.isEmptyIfStr(params)) {
            JSONObject paramsJson = JSONUtil.parseObj(params);
            search = (JSONObject)paramsJson.get("search");
            sort = (JSONObject)paramsJson.get("sort");

        }
        return R.ok().put("data", systemUserService.pageCorrelation(companies, current, pageSize, search, sort));
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
        getEnvironmentVariable();
        systemUser.setId(id);
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        if (id.equals(systemAdministratorId)) {
            if (systemUser.getIsEnabled() != null && !systemUser.isEnabled()) {
                return R.error(response, ExceptionCode.NOT_MODIFIED.getCode(), "不能禁用系统管理员！");
            }
            if (systemUser.getIsLocked() != null && !systemUser.isAccountNonLocked()) {
                return R.error(response, ExceptionCode.NOT_MODIFIED.getCode(), "不能锁定系统管理员！");
            }
        }
        if (systemUserService.updateById(systemUser)) {
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
        getEnvironmentVariable();
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
    @PutMapping("/system/user/{id}")
    @ApiOperation("修改处理系统用户")
    public R systemUserModifyHandle(@ApiParam(name = "要修改的用户名编号", required = true) @PathVariable Long id,
        @ApiParam(name = "系统用户对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) SystemUser systemUser) {
        getEnvironmentVariable();
        systemUser.setId(id);
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        if (!id.equals(systemAdministratorId)) {
            systemUser.setIsLocked(false).setIsEnabled(true);
        }
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
    @DeleteMapping("/system/user/{id}")
    @ApiOperation("通过系统用户编号删除系统用户")
    public R removeSystemUser(@ApiParam(name = "要删除的用户名编号", required = true) @PathVariable Long id) {
        getEnvironmentVariable();
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
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
    public R configurationBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        getEnvironmentVariable();
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            ids = ids.stream().filter(id -> !id.equals(systemUser.getId())).collect(Collectors.toList());
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
     * @return 系统权限表单列表JSON
     */
    @GetMapping("/system/permission/select-list")
    @ApiOperation("系统权限列表")
    public R systemPermissionSelectList() {
        List<SelectListVo> selectListVos = systemPermissionService.treeSelectList();
        SelectListVo selectListVo = new SelectListVo();
        selectListVo.setTitle("顶级权限").setValue(0L);
        selectListVos.add(0, selectListVo);
        return R.ok().put("data", selectListVos);
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
    @PutMapping("/system/permission")
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
    @DeleteMapping("/system/permission/{id}")
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
        systemUserVo.setPhoneNumber(systemUser.getPhoneNumber().getPhoneNumber());
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
}
