package com.github.phonenumbermanager.annotation;

import java.lang.annotation.*;

import com.github.phonenumbermanager.validator.PhoneNumberConstraintValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 验证联系方式注解
 *
 * @author 廿二月的天
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
    ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PhoneNumberValidator.List.class)
@Documented
@Constraint(validatedBy = {PhoneNumberConstraintValidator.class})
public @interface PhoneNumberValidator {
    String message() default "{com.github.phonenumbermanager.annotation.PhoneNumberValidator.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PhoneNumberValidator[] value();
    }
}
