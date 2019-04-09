package security.handler;

import com.alibaba.fastjson.JSONObject;
import constant.SystemConstant;
import org.springframework.security.core.Authentication;
import utils.CommonUtil;
import utils.DateUtil;
import www.entity.SystemUser;
import www.service.SystemUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * 登录认证成功后执行的操作
 *
 * @author 廿二月的天
 */
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private SystemUserService systemUserService;

    public void setSystemUserService(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        HttpSession session = httpServletRequest.getSession();
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        systemUser.setPassword(null);
        systemUser.setLoginTime(DateUtil.getTimestamp(new Date()));
        systemUser.setLoginIp(CommonUtil.getIp(httpServletRequest));
        try {
            systemUserService.updateObject(systemUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        systemUser.setPassword(null);
        session.setAttribute("systemUser", systemUser);
        httpServletResponse.setCharacterEncoding("UTF-8");
        String header = httpServletRequest.getHeader("X-Requested-With");
        if (SystemConstant.AJAX_HEADER_STATUS.equals(header)) {
            httpServletResponse.setHeader("Content-type", "text/json;charset=UTF-8");
            JSONObject returnObj = new JSONObject();
            returnObj.put("state", 1);
            returnObj.put("message", "登录成功！");
            httpServletResponse.getWriter().write(returnObj.toString());
            httpServletResponse.getWriter().flush();
        }
    }
}
