package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.UserRole;
import www.service.UserRoleService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统用户角色添加/更新验证
 *
 * @author 廿二月的天
 */
public class UserRoleInputValidator extends BaseInputValidator<UserRole> implements Validator {
    private UserRoleService userRoleService;

    public UserRoleInputValidator(UserRoleService userRoleService, HttpServletRequest request) {
        this.userRoleService = userRoleService;
        this.request = request;
    }

    @Override
    public boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "roleName", "role.roleName.required", "系统用户角色名称不能为空！");
        UserRole userRole = (UserRole) target;
        if (userRole.getHigherRole() < 0) {
            field = "higherRole";
            errorCode = "userRole.higherRole.errorCode";
            message = "系统用户上级角色错误！";
            return false;
        }
        if ("POST".equals(request.getMethod())) {
            try {
                List<UserRole> userRoles = userRoleService.findRolesByRoleName(userRole.getRoleName());
                if (userRoles != null && userRoles.size() > 0) {
                    field = "roleName";
                    errorCode = "userRole.roleName.errorCode";
                    message = "所填内容已存在！";
                    return false;
                }
            } catch (Exception e) {
                throw new BusinessException("系统用户角色验证失败！");
            }
        }
        return true;
    }
}
