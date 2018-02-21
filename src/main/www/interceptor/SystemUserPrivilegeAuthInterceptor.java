package www.interceptor;

import annotation.SystemUserAuth;
import exception.PrivilegeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.CommonUtil;
import www.entity.SystemUser;

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
            @SuppressWarnings("unchecked")
            Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            Integer systemAdministratorId = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_administrator_id"));
            // 判断是否为超级管理员，不是，进行权限验证
            if (!systemUser.getSystemUserId().equals(systemAdministratorId)) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Class<?> clazz = handlerMethod.getBean().getClass();
                SystemUserAuth authClass = clazz.getAnnotation(SystemUserAuth.class);
                SystemUserAuth authMethod = handlerMethod.getMethod().getAnnotation(SystemUserAuth.class);
                @SuppressWarnings("unchecked")
                Map<String, Set<String>> privilegeMap = (Map<String, Set<String>>) session.getAttribute("privilegeMap");
                Set<String> privilegeAuth = privilegeMap.get("privilegeAuth");
                String className = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
                // 有权限控制的就要检查
                if (authMethod != null) {
                    // 方法中有验证注解
                    if (!authMethod.unAuth()) {
                        String constraintAuth = handlerMethod.getMethod().getName();
                        if (!"".equals(authMethod.value())) {
                            constraintAuth = authMethod.value();
                        }
                        boolean flag;
                        // 判断是否强制验证注解
                        if (authMethod.enforce()) {
                            flag = !privilegeAuth.contains(constraintAuth);
                        } else {
                            flag = !privilegeAuth.contains(constraintAuth) && !privilegeAuth.contains(className);

                        }
                        // 提示用户没权限
                        if (flag) {
                            throw new PrivilegeException("您没有该权限！");
                        }
                    }
                } else if (authClass != null) {
                    // 控制器中存在验证注解
                    Set<String> privilegeParents = privilegeMap.get("privilegeParents");
                    if (!"".equals(authClass.value())) {
                        className = authClass.value();
                    }
                    // 提示用户没权限
                    if (!privilegeAuth.contains(className) && !privilegeParents.contains(className)) {
                        throw new PrivilegeException("您没有该权限！");
                    }
                }
            }
        }
        return super.preHandle(request, response, handler);
    }
}
