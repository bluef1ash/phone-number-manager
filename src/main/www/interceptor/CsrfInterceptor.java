package www.interceptor;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.CsrfTokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 防止CSRF攻击拦截器
 *
 * @author 廿二月的天
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 提交表单token校验
        HandlerMethod handlerMethod;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            return true;
        }
        Method method = handlerMethod.getMethod();
        VerifyCSRFToken verifyCSRFToken = method.getAnnotation(VerifyCSRFToken.class);
        // 如果配置了校验csrf token校验，则校验
        if (verifyCSRFToken != null) {
            // 是否为Ajax标志
            String xrq = request.getHeader("X-Requested-With");
            // 非法的跨站请求校验
            if (verifyCSRFToken.verify() && !CsrfTokenUtil.verifyCSRFToken(request, null)) {
                HttpSession session = request.getSession();
                Map<String, Object> map = new HashMap<>(3);
                Map<String, String> messageError = new HashMap<>(2);
                messageError.put("defaultMessage", "CSRF ERROR:无效的token，或者token过期！");
                map.put("state", 0);
                map.put("messageError", messageError);
                if (StringUtils.isEmpty(xrq)) {
                    // form表单提交，url get方式，刷新csrftoken并跳转提示页面
                    messageError.put("defaultMessage", "非法请求！");
                }
                CsrfTokenUtil.setTokenForSession(session);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSONString(map));
                response.flushBuffer();
                return false;
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        HttpSession session = request.getSession();
        // 第一次生成token
        boolean sessionIsNotEmpty = request.getSession(false) == null || StringUtils.isEmpty(CsrfTokenUtil.getTokenForSession(session, null));
        if (modelAndView != null && sessionIsNotEmpty) {
            CsrfTokenUtil.setTokenForSessionAndModel(session, modelAndView);
        } else {
            // 刷新token
            HandlerMethod handlerMethod;
            if (handler instanceof HandlerMethod) {
                handlerMethod = (HandlerMethod) handler;
            } else {
                return;
            }
            Method method = handlerMethod.getMethod();
            RefreshCsrfToken refreshAnnotation = method.getAnnotation(RefreshCsrfToken.class);
            // 跳转到一个新页面 刷新token
            String xrq = request.getHeader("X-Requested-With");
            if (modelAndView != null) {
                if (refreshAnnotation != null && refreshAnnotation.refresh() && StringUtils.isEmpty(xrq)) {
                    CsrfTokenUtil.setTokenForSessionAndModel(session, modelAndView);
                } else {
                    // 校验成功 刷新token 可以防止重复提交
                    VerifyCSRFToken verifyAnnotation = method.getAnnotation(VerifyCSRFToken.class);
                    if (verifyAnnotation != null && verifyAnnotation.verify()) {
                        if (StringUtils.isEmpty(xrq)) {
                            CsrfTokenUtil.setTokenForSessionAndModel(session, modelAndView);
                        } else {
                            response.setContentType("application/json;charset=UTF-8");
                        }
                    }
                }
            }
        }
    }
}
