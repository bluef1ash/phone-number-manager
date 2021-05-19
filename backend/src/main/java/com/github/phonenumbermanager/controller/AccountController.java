package com.github.phonenumbermanager.controller;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.GeetestLibUtil;
import com.github.phonenumbermanager.util.JwtTokenUtil;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.validator.AccountInputValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            binder.replaceValidators(new AccountInputValidator(request, systemUserService, configurationService, redisUtil));
        }
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Map<String, Object> login(@ApiParam(name = "系统用户对象", required = true) @Validated SystemUser systemUser, BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            jsonMap.put("messageErrors", allErrors);
            return jsonMap;
        }
        SystemUser user = (SystemUser) systemUserService.loadUserByUsername(systemUser.getUsername());
        String token = JwtTokenUtil.generateToken(user);
        jsonMap.put("state", 1);
        jsonMap.put("token", token);
        return jsonMap;
    }

    /**
     * 生成图案验证码数据
     *
     * @param request     HTTP请求对象
     * @param browserType 浏览器类型
     * @return 验证图案数据
     */
    @GetMapping("/getRecaptcha")
    @ApiOperation("生成图案验证码数据")
    public String captcha(HttpServletRequest request, @ApiParam(name = "浏览器类型") String browserType) {
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", browserType);
        param.put("ip_address", CommonUtil.getIp(request));
        int gtServerStatus = gtSdk.preProcess(param);
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        return gtSdk.getResponseStr();
    }

    /**
     * 刷新凭证Token
     *
     * @param token 现有Token
     * @return 已刷新的凭证
     */
    @GetMapping("/refreshToken")
    @ApiOperation("刷新凭证Token")
    public String refreshToken(@ApiParam(name = "现有Token") @RequestHeader(SystemConstant.HEADER_STRING) String token) {
        return systemUserService.refreshToken(token);
    }
}
