package www.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import www.entity.SystemUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 *
 * @author 廿二月的天
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        HandlerMethod handlerMethod;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            return true;
        }
        final String loginAction = "www.action.LoginAction";
        String className = handlerMethod.getBean().getClass().getName();
        if (systemUser == null && !loginAction.equals(className)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        } else if (systemUser != null && loginAction.equals(className)) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        return super.preHandle(request, response, handler);
    }
}
