package www.interceptor;

import annotation.SystemUserAuth;
import exception.PrivilegeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.CommonUtil;
import www.entity.SystemUser;
import www.entity.UserPrivilege;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;

/**
 * 系统用户权限拦截器
 *
 * @author 廿二月的天
 */
public class SystemUserPrivilegeAuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
            // 判断是否为超级管理员，不是，进行权限验证
            if (!systemUser.getSystemUserId().equals(systemAdministratorId)) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Class<?> clazz = handlerMethod.getBean().getClass();
                //SystemUserAuth authClass = clazz.getAnnotation(SystemUserAuth.class);
                SystemUserAuth authMethod = handlerMethod.getMethod().getAnnotation(SystemUserAuth.class);
                Set<UserPrivilege> userPrivileges = (Set<UserPrivilege>) session.getAttribute("userPrivileges");
                String className = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
                // 有权限控制的就要检查
                if (authMethod != null && !authMethod.unAuth()) {
                    // 方法中有验证注解
                    String constraintAuth = handlerMethod.getMethod().getName();
                    if (!"".equals(authMethod.value())) {
                        constraintAuth = authMethod.value();
                    }
                    // 判断是否强制验证注解
                    for (UserPrivilege userPrivilege : userPrivileges) {
                        if (authMethod.enforce()) {
                            if (!constraintAuth.equals(userPrivilege.getConstraintAuth())) {
                                throw new PrivilegeException("您没有该权限！");
                            }
                        } else {
                            if (!constraintAuth.equals(userPrivilege.getConstraintAuth()) && !className.equals(userPrivilege.getConstraintAuth())) {
                                throw new PrivilegeException("您没有该权限！");
                            }

                        }
                    }
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        super.postHandle(request, response, handler, modelAndView);
    }
}
