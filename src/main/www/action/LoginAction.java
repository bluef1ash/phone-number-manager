package www.action;

import java.awt.image.BufferedImage;
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
import utils.CaptchaUtil;
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
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/login")
public class LoginAction {
    @Resource
    private SystemUserService systemUserService;
    private static String sRand;

    /**
     * 登录页面
     *
     * @return 视图页面
     */
    @RequestMapping(method = RequestMethod.GET)
    @RefreshCsrfToken
    public String login() {
        return "login/login";
    }

    /**
     * 通过AJAX验证用户能否登录
     *
     * @param request HTTP请求对象
     * @param session session对象
     * @param systemUser 异步传输的用户实体对象
     * @param captcha 验证码字符串
     * @return JSON格式信息
     */
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> ajaxLogin(HttpServletRequest request, HttpSession session, SystemUser systemUser, @RequestParam("captcha") String captcha) {
        try {
            Map<String, Object> map = systemUserService.loginCheck(request, systemUser, captcha, sRand);
            if (map.containsKey("systemUser")) {
                session.setAttribute("systemUser", map.get("systemUser"));
                session.setAttribute("privilegeMap", map.get("privilegeMap"));
            }
            return map;
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 退出系统
     *
     * @param session session对象
     * @return 视图页面
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
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(HttpServletRequest request, HttpServletResponse response
    ) {
        try {
            // 禁止图片缓存
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/png");
            // 将图像输出到servlet输出流中
            BufferedImage captchaImage = CaptchaUtil.getImage(request);
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write(captchaImage, "png", sos);
            sos.close();
            sRand = (String) request.getAttribute("sRand");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
