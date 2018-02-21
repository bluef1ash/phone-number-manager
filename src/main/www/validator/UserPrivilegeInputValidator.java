package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.UserPrivilege;
import www.service.UserPrivilegeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 街道添加/更新验证
 *
 * @author 廿二月的天
 */
public class UserPrivilegeInputValidator extends BaseInputValidator implements Validator {
    private UserPrivilegeService userPrivilegeService;

    public UserPrivilegeInputValidator() {
    }

    public UserPrivilegeInputValidator(UserPrivilegeService userPrivilegeService, HttpServletRequest request) {
        this.userPrivilegeService = userPrivilegeService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserPrivilege.class);
    }

    @Override
    public boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "privilegeName", "userPrivilege.privilegeName.required", "系统用户权限名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "constraintAuth", "userPrivilege.constraintAuth.required", "系统用户权限约束名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "uri", "userPrivilege.uri.required", "系统用户权限访问地址不能为空！");
            UserPrivilege userPrivilege = (UserPrivilege) target;
            if (userPrivilege.getIsDisplay() != 0 || userPrivilege.getIsDisplay() != 1) {
                field = "isDisplay";
                errorCode = "userPrivilege.isDisplay.errorCode";
                message = "是否在菜单栏中显示错误！";
                return false;
            }
            if ("POST".equals(request.getMethod())) {
                List<UserPrivilege> userPrivileges = userPrivilegeService.findObjects(userPrivilege);
                if (userPrivileges != null && userPrivileges.size() > 0) {
                    field = "privilegeId";
                    errorCode = "userPrivilege.privilegeId.errorCode";
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
