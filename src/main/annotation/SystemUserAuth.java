package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统用户权限验证注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemUserAuth {

    /**
     * 验证的字段
     *
     * @return 需要验证的字段
     */
    String value() default "";

    /**
     * 是否强制验证
     *
     * @return 是否强制验证
     */
    boolean enforce() default false;

    /**
     * 是否不进行验证
     *
     * @return 是否不进行验证
     */
    boolean unAuth() default false;
}
