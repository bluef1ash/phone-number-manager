package main.interceptor;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import constant.CodeConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import utils.CsrfTokenUtil;
import main.entity.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 防止CSRF攻击拦截器
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        System.out.println("---------->" + request.getRequestURI());
//        System.out.println(request.getHeader("X-Requested-With"));
        // 提交表单token 校验
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        VerifyCSRFToken verifyCSRFToken = method.getAnnotation(VerifyCSRFToken.class);
        // 如果配置了校验csrf token校验，则校验
        if (verifyCSRFToken != null) {
            // 是否为Ajax标志
            String xrq = request.getHeader("X-Requested-With");
            // 非法的跨站请求校验
            if (verifyCSRFToken.verify() && !verifyCSRFToken(request)) {
                if (StringUtils.isEmpty(xrq)) {
                    // form表单提交，url get方式，刷新csrftoken并跳转提示页面
                    String csrftoken = CsrfTokenUtil.generate(request);
                    request.getSession().setAttribute("CSRFToken", csrftoken);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("非法请求");
                    response.flushBuffer();
                    return false;
                } else {
                    // 刷新CSRFToken，返回错误码，用于ajax处理，可自定义
                    String csrftoken = CsrfTokenUtil.generate(request);
                    request.getSession().setAttribute("CSRFToken", csrftoken);
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

        // 第一次生成token
        if (modelAndView != null) {
            if (request.getSession(false) == null || StringUtils.isEmpty((String) request.getSession(false).getAttribute("CSRFToken"))) {
                request.getSession().setAttribute("CSRFToken", CsrfTokenUtil.generate(request));
                return;
            }
        }
        // 刷新token
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RefreshCsrfToken refreshAnnotation = method.getAnnotation(RefreshCsrfToken.class);

        // 跳转到一个新页面 刷新token
        String xrq = request.getHeader("X-Requested-With");
        if (refreshAnnotation != null && refreshAnnotation.refresh() && StringUtils.isEmpty(xrq)) {
            request.getSession().setAttribute("CSRFToken", CsrfTokenUtil.generate(request));
            return;
        }

        // 校验成功 刷新token 可以防止重复提交
        VerifyCSRFToken verifyAnnotation = method.getAnnotation(VerifyCSRFToken.class);
        if (verifyAnnotation != null) {
            if (verifyAnnotation.verify()) {
                if (StringUtils.isEmpty(xrq)) {
                    request.getSession().setAttribute("CSRFToken", CsrfTokenUtil.generate(request));
                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("CSRFToken", CsrfTokenUtil.generate(request));
                    response.setContentType("application/json;charset=UTF-8");
                    OutputStream out = response.getOutputStream();
                    out.write((",'csrf':" + JSON.toJSONString(map) + "}").getBytes("UTF-8"));
                }
            }
        }
    }

    /**
     * 处理跨站请求伪造 针对需要登录后才能处理的请求,验证CSRFToken校验
     *
     * @param request
     */
    private boolean verifyCSRFToken(HttpServletRequest request) {

        // 请求中的CSRFToken
        String requstCSRFToken = request.getHeader("CSRFToken");
        if (StringUtils.isEmpty(requstCSRFToken)) {
            return false;
        }
        String sessionCSRFToken = (String) request.getSession().getAttribute("CSRFToken");
        if (StringUtils.isEmpty(sessionCSRFToken)) {
            return false;
        }
        return requstCSRFToken.equals(sessionCSRFToken);
    }
}
