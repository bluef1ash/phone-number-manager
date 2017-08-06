package main.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import annotation.AvoidDuplicateSubmission;
import exception.IllegalOperationException;
/**
 * 防止重复提交表单
 *
 */
public class AvoidDuplicateSubmissionInterceptor extends HandlerInterceptorAdapter {
	private static final String AVOIDDUPLICATESUBMISSIONSESSIONNAME = "submissionToken";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
            AvoidDuplicateSubmission annotation = ((HandlerMethod) handler).getMethod().getAnnotation(AvoidDuplicateSubmission.class);
            if (annotation != null) {
                if (annotation.needSaveToken()) {
                    request.getSession(true).setAttribute(AVOIDDUPLICATESUBMISSIONSESSIONNAME, UUID.randomUUID().toString());
                }
                if (annotation.needRemoveToken()) {
                	String serverToken = (String) request.getSession(true).getAttribute(AVOIDDUPLICATESUBMISSIONSESSIONNAME);
            		String clinetToken = request.getParameter(AVOIDDUPLICATESUBMISSIONSESSIONNAME);
                    request.getSession(true).removeAttribute(AVOIDDUPLICATESUBMISSIONSESSIONNAME);
                    if (serverToken == null || clinetToken == null || !serverToken.equals(clinetToken)) {
                    	throw new IllegalOperationException("不能重复提交！");
                    }
                }
            }
		}
        return super.preHandle(request, response, handler);
    }
}