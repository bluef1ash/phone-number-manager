package la.isx.phone_number_manager.main.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import la.isx.phone_number_manager.main.entity.SystemUser;
/**
 * 登录拦截器
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
		if (systemUser == null) {
			response.sendRedirect(request.getContextPath() + "/jsp/login/login.jsp");
			return false;
		}
		return super.preHandle(request, response, handler);
	}
}