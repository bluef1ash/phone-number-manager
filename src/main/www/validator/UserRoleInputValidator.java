package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.Subdistrict;
import www.entity.UserRole;
import www.service.SubdistrictService;
import www.service.UserRoleService;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统用户角色添加/更新验证
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
     * @param userRole
     * @return
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
