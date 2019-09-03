package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SystemUserService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统用户添加/更新验证
 *
 * @author 廿二月的天
 */
public class SystemUserInputValidator extends BaseInputValidator<SystemUser> implements Validator {
    private SystemUserService systemUserService;

    public SystemUserInputValidator(SystemUserService systemUserService, HttpServletRequest request) {
        this.systemUserService = systemUserService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "username", "systemUser.username.required", "系统用户名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "locked", "systemUser.locked.required", "系统用户是否锁定不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "roleId", "systemUser.roleId.required", "系统用户角色不能为空！");
            SystemUser systemUser = (SystemUser) target;
            if (RequestMethod.POST.toString().equals(request.getMethod())) {
                SystemUser systemUserDb = systemUserService.find(systemUser.getUsername());
                if (systemUserDb != null && systemUserDb.getId() > 0) {
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
            return true;
        } catch (Exception e) {
            throw new BusinessException("系统用户验证失败！");
        }
    }
}
