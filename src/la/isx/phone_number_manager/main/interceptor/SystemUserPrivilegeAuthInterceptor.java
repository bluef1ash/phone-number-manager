package la.isx.phone_number_manager.main.interceptor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import la.isx.phone_number_manager.annotation.SystemUserAuth;
import la.isx.phone_number_manager.exception.PrivilegeException;
/**
 * 系统用户权限拦截器
 *
 */
public class SystemUserPrivilegeAuthInterceptor extends HandlerInterceptorAdapter {
    @Override  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
        	Class<?> clazz = ((HandlerMethod) handler).getBean().getClass();
        	SystemUserAuth authClass = clazz.getAnnotation(SystemUserAuth.class);
        	SystemUserAuth authMethod = ((HandlerMethod) handler).getMethod().getAnnotation(SystemUserAuth.class);
        	@SuppressWarnings("unchecked")
        	Map<String, Set<String>> privilegeMap = (Map<String, Set<String>>) request.getSession().getAttribute("privilegeMap");
        	Set<String> privilegeAuth = privilegeMap.get("privilegeAuth");
        	String className = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
            if (authMethod != null) {// 有权限控制的就要检查
            	if (!authMethod.unAuth()) {
	            	String constraintAuth = ((HandlerMethod) handler).getMethod().getName();
	            	if (authMethod.value() != null && ! "".equals(authMethod.value())) {
	            		constraintAuth = authMethod.value();
					}
	            	boolean flag = false;
	            	if (authMethod.enforce()) {
	            		flag = ! privilegeAuth.contains(constraintAuth);
					} else {
						flag = ! privilegeAuth.contains(constraintAuth) && ! privilegeAuth.contains(className);
						
					}
	                if (flag) {// 提示用户没权限
	                    throw new PrivilegeException("您没有该权限！");
	                }
            	}
            } else if (authClass != null) {
            	Set<String> privilegeParents = privilegeMap.get("privilegeParents");
            	if (authClass.value() != null && ! "".equals(authClass.value())) {
            		className = authClass.value();
				}
                if (! privilegeAuth.contains(className) && ! privilegeParents.contains(className)) {// 提示用户没权限
                    throw new PrivilegeException("您没有该权限！");
                }
			}
        }
        return super.preHandle(request, response, handler);
    }
}