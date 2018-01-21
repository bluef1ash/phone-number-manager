package www.interceptor;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import constant.CodeConstant;
import exception.CsrfException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.CsrfTokenUtil;
import www.entity.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 防止CSRF攻击拦截器
 *
 * @author 廿二月的天
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 提交表单token校验
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        VerifyCSRFToken verifyCSRFToken = method.getAnnotation(VerifyCSRFToken.class);
        // 如果配置了校验csrf token校验，则校验
        if (verifyCSRFToken != null) {
            // 是否为Ajax标志
            String xrq = request.getHeader("X-Requested-With");
            // 非法的跨站请求校验
            if (verifyCSRFToken.verify() && !CsrfTokenUtil.verifyCSRFToken(request, null)) {
                HttpSession session = request.getSession();
                if (StringUtils.isEmpty(xrq)) {
                    // form表单提交，url get方式，刷新csrftoken并跳转提示页面
                    CsrfTokenUtil.setTokenForSession(session);
                    throw new CsrfException("非法请求！");
                } else {
                    // 刷新CSRFToken，返回错误码，用于ajax处理，可自定义
                    CsrfTokenUtil.setTokenForSession(session);
                    ResultCode rc = CodeConstant.CSRF_ERROR;
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print(JSON.toJSONString(rc));
                    response.flushBuffer();
                    return false;
                }
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
            return;
        }
        // 刷新token
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RefreshCsrfToken refreshAnnotation = method.getAnnotation(RefreshCsrfToken.class);
        // 跳转到一个新页面 刷新token
        String xrq = request.getHeader("X-Requested-With");
        if (refreshAnnotation != null && refreshAnnotation.refresh() && StringUtils.isEmpty(xrq)) {
            assert modelAndView != null;
            CsrfTokenUtil.setTokenForSessionAndModel(session, modelAndView);
            return;
        }
        // 校验成功 刷新token 可以防止重复提交
        VerifyCSRFToken verifyAnnotation = method.getAnnotation(VerifyCSRFToken.class);
        if (verifyAnnotation != null) {
            if (verifyAnnotation.verify()) {
                if (StringUtils.isEmpty(xrq)) {
                    assert modelAndView != null;
                    CsrfTokenUtil.setTokenForSessionAndModel(session, modelAndView);
                } else {
                    response.setContentType("application/json;charset=UTF-8");
                }
            }
        }
    }
}
