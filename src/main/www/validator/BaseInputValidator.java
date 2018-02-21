package www.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础表单验证
 *
 * @author 廿二月的天
 */
public abstract class BaseInputValidator implements Validator {
    protected String message;
    protected String field;
    protected HttpServletRequest request;
    protected String errorCode;

    @Override
    public void validate(Object target, Errors errors) {
        if (!checkInput(target, errors)) {
            errors.rejectValue(field, errorCode, message);
        }
    }

    /**
     * 验证输入数据
     *
     * @param target 浏览器传入的对象
     * @param errors 错误对象
     * @return 验证是否成功
     */
    protected abstract boolean checkInput(Object target, Errors errors);
}
