package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.Subdistrict;
import www.entity.UserPrivilege;
import www.service.SubdistrictService;
import www.service.UserPrivilegeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 街道添加/更新验证
 */
public class UserPrivilegeInputValidator implements Validator {
    private String message;
    private String field;
    private String errorCode;
    private UserPrivilegeService userPrivilegeService;
    private HttpServletRequest request;

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
    public void validate(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "privilegeName", "userPrivilege.privilegeName.required", "系统用户权限名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "constraintAuth", "userPrivilege.constraintAuth.required", "系统用户权限约束名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "uri", "userPrivilege.uri.required", "系统用户权限访问地址不能为空！");
            UserPrivilege userPrivilege = (UserPrivilege) target;
            if (!checkInput(userPrivilege)) {
                errors.rejectValue(field, errorCode, message);
            }
        } catch (Exception e) {
            throw new BusinessException("系统用户权限验证失败！");
        }
    }

    /**
     * 验证输入数据
     *
     * @param userPrivilege
     * @return
     */
    private Boolean checkInput(UserPrivilege userPrivilege) throws Exception {
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
    }
}
