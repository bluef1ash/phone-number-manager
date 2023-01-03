package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.annotation.PhoneNumberValidator;

import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
