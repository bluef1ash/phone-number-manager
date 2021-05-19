package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.ConfigurationService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配置添加/更新验证
 *
 * @author 廿二月的天
 */
public class ConfigurationInputValidator extends BaseInputValidator<Configuration> {
    private final ConfigurationService configurationService;

    public ConfigurationInputValidator(ConfigurationService configurationService, HttpServletRequest request) {
        this.configurationService = configurationService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "key", "configuration.key.required", "配置项名称不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "value", "configuration.value.required", "配置项值不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "description", "configuration.description.required", "配置项描述不能为空！");
        Configuration configuration = (Configuration) target;
        try {
            // 配置项重复
            List<Configuration> isRepeat = configurationService.get(configuration);
            if (isRepeat != null && isRepeat.size() > 0) {
                field = "key";
                errorCode = "configuration.key.errorCode";
                message = "所填内容已存在！";
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统配置验证失败！");
        }
    }
}
