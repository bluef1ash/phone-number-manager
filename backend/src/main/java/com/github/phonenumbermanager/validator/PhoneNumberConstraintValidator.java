package com.github.phonenumbermanager.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.github.phonenumbermanager.entity.PhoneNumber;

import cn.hutool.core.util.PhoneUtil;

/**
 * 验证联系方式注解实现
 *
 * @author 廿二月的天
 */
public class PhoneNumberConstraintValidator implements ConstraintValidator<PhoneNumberValidator, List<PhoneNumber>> {

    @Override
    public boolean isValid(List<PhoneNumber> phoneNumbers, ConstraintValidatorContext constraintValidatorContext) {
        for (PhoneNumber phoneNumber : phoneNumbers) {
            if (phoneNumber == null || StringUtils.isEmpty(phoneNumber.getPhoneNumber())
                || PhoneUtil.isPhone(phoneNumber.getPhoneNumber())) {
                return false;
            }
        }
        return true;
    }
}
