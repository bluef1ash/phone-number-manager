package com.github.phonenumbermanager.validator;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

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
    String message() default "{com.github.phonenumbermanager.validator.PhoneNumberValidator.message}";

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
