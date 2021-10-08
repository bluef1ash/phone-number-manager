package com.github.phonenumbermanager.controller;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.GeetestLibUtil;
import com.github.phonenumbermanager.util.R;

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
    private SystemUserService systemUserService;

    /**
     * 用户登录
     *
     * @param systemUser
     *            前端传入的用户对象
     * @return JSON对象
     * @throws LoginException
     *             登录异常
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@ApiParam(name = "系统用户对象", required = true) @RequestBody @Validated SystemUser systemUser)
        throws LoginException {
        Authentication authentication =
            systemUserService.authentication(systemUser.getUsername(), systemUser.getPassword());
        SystemUser principal = (SystemUser)authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(SystemConstant.SYSTEM_USER_KEY, principal);
        claims.put(SystemConstant.CLAIM_KEY_CREATED, new Date());
        return R.ok().put("token",
            JWTUtil.createToken(claims, SystemConstant.BASE64_SECRET.getBytes(StandardCharsets.UTF_8)));
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
    public R logout() {
        return R.ok();
    }
}
