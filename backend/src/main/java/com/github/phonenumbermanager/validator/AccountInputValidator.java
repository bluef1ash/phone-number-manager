package com.github.phonenumbermanager.validator;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.RedisUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账号验证
 *
 * @author 廿二月的天
 */
public class AccountInputValidator extends BaseInputValidator<SystemUser> {
    private final SystemUserService systemUserService;
    private final ConfigurationService configurationService;
    private final RedisUtil redisUtil;

    public AccountInputValidator(HttpServletRequest request, SystemUserService systemUserService, ConfigurationService configurationService, RedisUtil redisUtil) {
        this.request = request;
        this.systemUserService = systemUserService;
        this.configurationService = configurationService;
        this.redisUtil = redisUtil;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "username", "systemUser.username.required", "用户名称不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "password", "systemUser.password.required", "用户密码不能为空！");
        SystemUser systemUser = (SystemUser) target;
        QueryWrapper<SystemUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", systemUser.getUsername());
        SystemUser user = systemUserService.getOne(wrapper);
        if (user == null) {
            errorCode = "systemUser.username.error";
            field = "username";
            message = systemUser.getUsername() + "系统用户不存在！";
            return false;
        }
        @SuppressWarnings("all") Map<String, Object> configurationsMap = (Map<String, Object>) redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        if (configurationsMap == null) {
            List<Configuration> configurations = configurationService.list(null);
            configurationsMap = new HashMap<>(configurations.size() + 1);
            for (Configuration configuration : configurations) {
                configurationsMap.put(configuration.getKey(), configuration.getValue());
            }
            redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, configurationsMap);
        }
        Integer systemIsActive = Convert.toInt(configurationsMap.get("system_is_active"));
        Long systemAdministratorId = Convert.toLong(configurationsMap.get("system_administrator_id"));
        if (systemIsActive == 0 && !systemAdministratorId.equals(user.getId())) {
            errorCode = "systemUser.username.error";
            field = "username";
            message = "该系统已经禁止登录，请联系管理员！";
            return false;
        }
        return true;
    }
}
