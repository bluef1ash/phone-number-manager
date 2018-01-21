package utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 防止CSRF攻击
 *
 * @author 廿二月的天
 */
public class CsrfTokenUtil {

    private CsrfTokenUtil() {
    }

    /**
     * token参数名称
     */
    private static final String CSRF_PARAM_NAME = "_token";

    /**
     * token在session中的名称
     */
    private static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CsrfTokenUtil.class.getName() + ".tokenval";

    /**
     * 从Session中获取Token
     *
     * @param session    session对象
     * @param sessionKey token在session中的名称
     * @return 新的token
     */
    public static String getTokenForSession(HttpSession session, String sessionKey) {
        String token = null;
        if (sessionKey == null) {
            sessionKey = CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
        }
        if (session == null) {
            return null;
        }
        // I cannot allow more than one token on a session - in the case of two requests trying to init the token concurrently
        synchronized (CsrfTokenUtil.class) {
            token = (String) session.getAttribute(sessionKey);
            if (null == token) {
                token = generate();
                session.setAttribute(sessionKey, token);
            }
        }
        return token;
    }

    /**
     * 设置Token到Session中
     *
     * @param session session对象
     */
    public static void setTokenForSession(HttpSession session) {
        session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, generate());
        session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
    }

    /**
     * 设置token到session和模型中
     *
     * @param session session对象
     * @param model 模型对象
     */
    public static void setTokenForSessionAndModel(HttpSession session, ModelAndView model) {
        String token = generate();
        session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
        model.addObject(CSRF_PARAM_NAME, token);
    }

    /**
     * 从提交的表单获取Token
     *
     * @param request HTTP请求对象
     * @return 新的token
     * @throws UnsupportedEncodingException
     */
    public static String getTokenFromRequest(HttpServletRequest request) throws UnsupportedEncodingException {
        String token = request.getParameter(CSRF_PARAM_NAME);
        if (StringUtils.isNotEmpty(token)) {
            return new String(token.getBytes("iso-8859-1"), "utf-8");
        }
        return null;
    }

    /**
     * 生成Token
     *
     * @return 生成的token值
     */
    private static String generate() {
        return UUID.randomUUID().toString();
    }

    /**
     * Token校验
     *
     * @param request HTTP请求对象
     * @param sessionKey token在session中的名称
     * @return 是否效验成功
     * @throws UnsupportedEncodingException
     */
    public static boolean verifyCSRFToken(HttpServletRequest request, String sessionKey) throws UnsupportedEncodingException {
        String requestToken = null;
        // 请求中的CSRFToken
        String requestCSRFToken = request.getHeader(CSRF_PARAM_NAME);
        if (StringUtils.isNotEmpty(requestCSRFToken)) {
            requestToken = requestCSRFToken;
        }
        String formToken = getTokenFromRequest(request);
        if (StringUtils.isNotEmpty(formToken)) {
            requestToken = formToken;
        }
        String sessionCSRFToken = getTokenForSession(request.getSession(), sessionKey);
        return !StringUtils.isEmpty(sessionCSRFToken) && !StringUtils.isEmpty(requestToken) && requestToken.equals(sessionCSRFToken);
    }

    public static String getCsrfParamName() {
        return CSRF_PARAM_NAME;
    }

    public static String getCsrfTokenForSessionAttrName() {
        return CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
    }

}
