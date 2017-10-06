package main.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ValidFunctionsInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String methodName = ((HandlerMethod) handler).getMethod().getName();
        request.getSession().setAttribute("validFunction", methodName);
        return super.preHandle(request, response, handler);
    }
}
