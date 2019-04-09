package security.handler;

import com.alibaba.fastjson.JSONObject;
import constant.SystemConstant;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 用户登录认证失败后执行的操作
 *
 * @author 廿二月的天
 */
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletRequest.getSession().invalidate();
        String header = httpServletRequest.getHeader("X-Requested-With");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        if (SystemConstant.AJAX_HEADER_STATUS.equals(header)) {
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 0);
            returnObj.put("message", "登录失败！");
            httpServletResponse.getWriter().print(Arrays.toString(returnObj.toString().getBytes(StandardCharsets.UTF_8)));
            httpServletResponse.getWriter().flush();
        }
    }
}
