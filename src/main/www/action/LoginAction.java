package www.action;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import www.entity.Captcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import exception.BusinessException;
import www.entity.SystemUser;
import www.service.SystemUserService;

/**
 * 登录控制器
 */
@Controller
@RequestMapping("/login")
public class LoginAction {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private Captcha captchaEntity;
    private static String sRand;

    /**
     * 登录页面
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @RefreshCsrfToken
    public String login() {
        return "login/login";
    }

    /**
     * 通过AJAX验证用户能否登录
     *
     * @param systemUser 异步传输的用户实体对象
     * @return JSON格式信息
     */
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> ajaxLogin(HttpServletRequest request, HttpSession session, SystemUser systemUser, @RequestParam("captcha") String captcha) {
        Map<String, Object> map = null;
        if (systemUser != null) {
            try {
                map = systemUserService.loginCheck(request, systemUser, captcha, sRand);
                if (map.containsKey("systemUser")) {
                    session.setAttribute("systemUser", map.get("systemUser"));
                    session.setAttribute("privilegeMap", map.get("privilegeMap"));
                }
            } catch (Exception e) {
                throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
            }
        }
        return map;
    }

    /**
     * 退出系统
     *
     * @param session
     * @return
     */
    @RequestMapping("/loginout")
    public String loginOut(HttpSession session) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        if (systemUser != null) {
            session.invalidate();
            return "redirect:/login.action";
        }
        return "redirect:/index.action";
    }

    /**
     * 图片验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(HttpServletRequest request, HttpServletResponse response
    ) {
        try {
            // 禁止图片缓存
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            // 将图像输出到servlet输出流中
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write(captchaEntity.getImage(request), "jpeg", sos);
            sos.close();
            sRand = (String) request.getAttribute("sRand");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
