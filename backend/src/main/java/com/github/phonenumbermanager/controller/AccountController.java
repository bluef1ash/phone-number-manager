package com.github.phonenumbermanager.controller;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.GeetestLibUtil;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.validator.AccountInputValidator;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.jwt.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 账号控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/account")
@Api(tags = "账号控制器")
public class AccountController extends BaseController {
    @Resource
    private HttpServletRequest request;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private ConfigurationService configurationService;
    @Resource
    private RedisUtil redisUtil;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            binder.replaceValidators(
                new AccountInputValidator(request, systemUserService, configurationService, redisUtil));
        }
    }

    /**
     * 用户登录
     *
     * @param systemUser
     *            前端传入的用户对象
     * @param bindingResult
     *            验证结果对象
     * @return JSON对象
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Map<String, Object> login(@ApiParam(name = "系统用户对象", required = true) @Validated SystemUser systemUser,
        BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            jsonMap.put("messageErrors", allErrors);
            return jsonMap;
        }
        Authentication authentication =
            systemUserService.authentication(systemUser.getUsername(), systemUser.getPassword());
        Map<String, Object> claims = new HashMap<>(2);
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        claims.put(SystemConstant.USERNAME_KEY, systemUser.getUsername());
        claims.put(SystemConstant.AUTHORITIES_KEY, authorities);
        claims.put(SystemConstant.CLAIM_KEY_CREATED, new Date());
        R r = new R();
        jsonMap.put("state", 1);
        jsonMap.put("token",
            JWTUtil.createToken(claims, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8)));
        return jsonMap;
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
    @GetMapping("/getRecaptcha")
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
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Map<String, Object> logout() {
        Map<String, Object> jsonMap = new HashMap<>(1);
        return jsonMap;
    }
}
