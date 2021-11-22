package com.github.phonenumbermanager.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cn.hutool.core.util.PhoneUtil;

/**
 * 验证联系方式注解实现
 *
 * @author 廿二月的天
 */
public class PhoneNumberConstraintValidator implements ConstraintValidator<PhoneNumberValidator, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return PhoneUtil.isPhone(phoneNumber);
    }
}
