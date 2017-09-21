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
 */
public class CsrfTokenUtil {

    private CsrfTokenUtil() {
    }

    /**
     * The token parameter name
     */
    private static final String CSRF_PARAM_NAME = "_token";

    /**
     * The location on the session which stores the token
     */
    private static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CsrfTokenUtil.class.getName() + ".tokenval";

    /**
     * 从Session中获取Token
     *
     * @param session
     * @param sessionKey
     * @return
     */
    public static String getTokenForSession(HttpSession session, String sessionKey) {
        String token = null;
        if (sessionKey == null) {
            sessionKey = CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
        }
        // I cannot allow more than one token on a session - in the case of two
        // requests trying to
        // init the token concurrently
        synchronized (session) {
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
     * @param session
     * @return
     */
    public static String setTokenForSession(HttpSession session) {
        session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, generate());
        return (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
    }

    public static String setTokenForSessionAndModel(HttpSession session, ModelAndView model) {
        String token = generate();
        session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
        model.addObject(CSRF_PARAM_NAME, token);
        return token;
    }

    /**
     * 从提交的表单获取Token
     *
     * @param request
     * @return
     * @exception
     */
    public static String getTokenFromRequest(HttpServletRequest request) throws UnsupportedEncodingException {
        String token = request.getParameter(CSRF_PARAM_NAME);
        if (StringUtils.isNotEmpty(token)) {
            return new String(token.getBytes("iso-8859-1"), "utf-8");
        }
        return null;
    }

    /**
     * 算Token
     *
     * @return
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }

    /**
     * Token校验
     *
     * @param request
     * @param sessionKey
     * @return
     * @exception
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
        if (StringUtils.isEmpty(sessionCSRFToken) || StringUtils.isEmpty(requestToken)) {
            return false;
        }
        return requestToken.equals(sessionCSRFToken);
    }

    public static String getCsrfParamName() {
        return CSRF_PARAM_NAME;
    }

    public static String getCsrfTokenForSessionAttrName() {
        return CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
    }

}
