package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private static final String CSRF_PARAM_NAME = "CSRFToken";

    /**
     * The location on the session which stores the token
     */
    private static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CsrfTokenUtil.class.getName() + ".tokenval";

    public static String getTokenForSession(HttpSession session) {
        String token = null;

        // I cannot allow more than one token on a session - in the case of two
        // requests trying to
        // init the token concurrently
        synchronized (session) {
            token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
            if (null == token) {
                token = UUID.randomUUID().toString();
                session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
            }
        }
        return token;
    }

    /**
     * Extracts the token value from the session
     *
     * @param request
     * @return
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        return request.getParameter(CSRF_PARAM_NAME);
    }

    public static String getCsrfTokenForSessionAttrName() {
        return CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
    }

    public static String generate(HttpServletRequest request) {

        return UUID.randomUUID().toString();
    }
}