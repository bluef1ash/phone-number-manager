package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跨站请求仿照注解
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyCSRFToken {

    /**
     * 需要验证防跨站请求
     *
     * @return 是否需要验证
     */
    public abstract boolean verify() default true;

}
