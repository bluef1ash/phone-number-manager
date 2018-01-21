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
public class UserRoleInputValidator implements Validator {
    private String message;
    private String field;
    private String errorCode;
    private UserRoleService userRoleService;
    private HttpServletRequest request;

    public UserRoleInputValidator() {
    }

    public UserRoleInputValidator(UserRoleService userRoleService, HttpServletRequest request) {
        this.userRoleService = userRoleService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserRole.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "roleName", "role.roleName.required", "系统用户角色名称不能为空！");
            UserRole userRole = (UserRole) target;
            if (!checkInput(userRole)) {
                errors.rejectValue(field, errorCode, message);
            }
        } catch (Exception e) {
            throw new BusinessException("系统用户角色验证失败！");
        }
    }

    /**
     * 验证输入数据
     *
     * @param userRole 需要验证的系统用户角色对象
     * @return 验证是否成功
     * @throws Exception 数据库操作异常
     */
    private Boolean checkInput(UserRole userRole) throws Exception {
        if (userRole.getHigherRole() < 0) {
            field = "higherRole";
            errorCode = "userRole.higherRole.errorCode";
            message = "系统用户上级角色错误！";
            return false;
        }
        if ("POST".equals(request.getMethod())) {
            List<UserRole> userRoles = userRoleService.findRolesByRoleName(userRole.getRoleName());
            if (userRoles != null && userRoles.size() > 0) {
                field = "roleName";
                errorCode = "userRole.roleName.errorCode";
                message = "所填内容已存在！";
                return false;
            }
        }
        return true;
    }
}
