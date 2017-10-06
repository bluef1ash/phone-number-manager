package main.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import main.entity.SystemUser;
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
			response.sendRedirect(request.getContextPath() + "/login.action");
			return false;
		}
		return super.preHandle(request, response, handler);
	}
}
