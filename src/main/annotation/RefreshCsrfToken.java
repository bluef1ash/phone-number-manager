package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跨站请求仿照注解 刷新CSRFToken
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshCsrfToken {

    /**
     * 刷新token
     *
     * @return 是否刷新
     */
    public abstract boolean refresh() default true;
}
