package www.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 将当前控制器名称存入session以用于验证的拦截器
 *
 * @author 廿二月的天
 */
public class ValidFunctionsInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 将当前控制器的方法名存入session
        String methodName = ((HandlerMethod) handler).getMethod().getName();
        request.getSession().setAttribute("validFunction", methodName);
        return super.preHandle(request, response, handler);
    }
}
