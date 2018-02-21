package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.Configuration;
import www.service.ConfigurationService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配置添加/更新验证
 *
 * @author 廿二月的天
 */
public class ConfigurationInputValidator extends BaseInputValidator implements Validator {
    private ConfigurationService configurationService;

    public ConfigurationInputValidator() {
    }

    public ConfigurationInputValidator(ConfigurationService configurationService, HttpServletRequest request) {
        this.configurationService = configurationService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Configuration.class);
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "key", "configuration.key.required", "配置项名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "value", "configuration.value.required", "配置项值不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "description", "configuration.description.required", "配置项描述不能为空！");
            Configuration configuration = (Configuration) target;
            // 配置项重复
            List<Configuration> isRepeat = configurationService.findObjects(configuration);
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
