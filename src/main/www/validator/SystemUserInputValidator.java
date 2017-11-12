package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.SystemUser;
import www.service.SystemUserService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统用户添加/更新验证
 */
public class SystemUserInputValidator implements Validator {
    private String message;
    private String field;
    private SystemUserService systemUserService;
    private HttpServletRequest request;

    public SystemUserInputValidator() {
    }

    public SystemUserInputValidator(SystemUserService systemUserService, HttpServletRequest request) {
        this.systemUserService = systemUserService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemUser.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "username", "systemUser.username.required", "系统用户名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "isLocked", "systemUser.isLocked.required", "系统用户是否锁定不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "roleId", "systemUser.roleId.required", "系统用户角色不能为空！");
            SystemUser systemUser = (SystemUser) target;
            if ("POST".equals(request.getMethod())) {
                List<SystemUser> systemUsers = systemUserService.findSystemUserByUserName(systemUser.getUsername());
                if (systemUsers.size() > 0) {
                    errors.rejectValue("username", "systemUser.username.exists", "此用户已存在，请更改用户名称！");
                }
                ValidationUtils.rejectIfEmpty(errors, "password", "systemUser.password.required");
            }
            if (systemUser.getPassword() != null && !"".equals(systemUser.getPassword())) {
                if (systemUser.getPassword().length() < 6) {
                    errors.rejectValue("password", "systemUser.password.length", "系统用户密码不能少于6位！");
                }
                String confirmPassword = request.getParameter("confirmPassword");
                if (!confirmPassword.equals(systemUser.getPassword())) {
                    errors.rejectValue("password", "systemUser.password.confirm", "系统用户密码与确认密码必须相同！");
                }
            } else {
                systemUser.setPassword(null);
            }
        } catch (Exception e) {
            throw new BusinessException("系统用户验证失败！");
        }
    }
}
