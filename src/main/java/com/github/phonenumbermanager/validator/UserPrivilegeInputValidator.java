package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 街道添加/更新验证
 *
 * @author 廿二月的天
 */
public class UserPrivilegeInputValidator extends BaseInputValidator<UserPrivilege> implements Validator {
    private final UserPrivilegeService userPrivilegeService;

    public UserPrivilegeInputValidator(UserPrivilegeService userPrivilegeService, HttpServletRequest request) {
        this.userPrivilegeService = userPrivilegeService;
        this.request = request;
    }

    @Override
    public boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "name", "userPrivilege.name.required", "系统用户权限名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "constraintAuth", "userPrivilege.constraintAuth.required", "系统用户权限约束名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "uri", "userPrivilege.uri.required", "系统用户权限访问地址不能为空！");
            UserPrivilege userPrivilege = (UserPrivilege) target;
            if (RequestMethod.POST.toString().equals(request.getMethod())) {
                List<UserPrivilege> userPrivileges = userPrivilegeService.get(userPrivilege);
                if (userPrivileges != null && userPrivileges.size() > 0) {
                    field = "id";
                    errorCode = "userPrivilege.id.errorCode";
                    message = "所填内容已存在！";
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException("系统用户权限验证失败！");
        }
    }
}
