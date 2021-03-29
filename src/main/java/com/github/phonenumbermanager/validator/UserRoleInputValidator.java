package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.UserRoleService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统用户角色添加/更新验证
 *
 * @author 廿二月的天
 */
public class UserRoleInputValidator extends BaseInputValidator<UserRole> implements Validator {
    private final UserRoleService userRoleService;

    public UserRoleInputValidator(UserRoleService userRoleService, HttpServletRequest request) {
        this.userRoleService = userRoleService;
        this.request = request;
    }

    @Override
    public boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "role.name.required", "系统用户角色名称不能为空！");
        UserRole userRole = (UserRole) target;
        if (userRole.getParentId() < 0) {
            field = "parentId";
            errorCode = "userRole.parentId.errorCode";
            message = "系统用户上级角色错误！";
            return false;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            try {
                UserRole userRoleDb = userRoleService.getById(userRole.getName());
                if (userRoleDb != null && userRoleDb.getId() != null) {
                    field = "name";
                    errorCode = "userRole.name.errorCode";
                    message = "所填内容已存在！";
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("系统用户角色验证失败！");
            }
        }
        return true;
    }
}
