package com.github.phonenumbermanager.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;

/**
 * 基础表单验证
 *
 * @author 廿二月的天
 */
public abstract class BaseInputValidator<T> implements Validator {
    protected String message;
    protected String field;
    protected HttpServletRequest request;
    String errorCode;

    @Override
    public void validate(Object target, Errors errors) {
        if (!checkInput(target, errors)) {
            errors.rejectValue(field, errorCode, message);
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        return clazz.equals(aClass);
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
