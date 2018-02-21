package www.action;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.CaptchaUtil;
import org.springframework.stereotype.Controller;

import www.entity.SystemUser;
import www.validator.LoginInputValidator;
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
    private String captcha;
    private final HttpServletRequest request;

    @Autowired
    public LoginAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("ajaxLogin".equals(validFunction)) {
            binder.replaceValidators(new LoginInputValidator(systemUserService, request, captcha));
        }
    }

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
     * @param bindingResult 错误信息对象
     * @return JSON格式信息
     */
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> ajaxLogin(HttpServletRequest request, HttpSession session, @Validated SystemUser systemUser, BindingResult bindingResult) {
        Map<String, Object> map;
        if (bindingResult.hasErrors()) {
            map = new HashMap<>(3);
            map.put("state", 0);
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            map.put("messageError", allErrors.get(0));
        } else {
            try {
                map = systemUserService.login(request, systemUser);
                if (map.containsKey("systemUser")) {
                    session.setAttribute("systemUser", map.get("systemUser"));
                    session.setAttribute("privilegeMap", map.get("privilegeMap"));
                    session.setAttribute("configurationsMap", map.get("configurationsMap"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, String> errorMap = new HashMap<>(2);
                errorMap.put("defaultMessage", "登录失败，请稍后再试！");
                map = new HashMap<>(3);
                map.put("state", 0);
                map.put("messageErrors", errorMap);
            }
        }
        return map;
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
            captcha = (String) request.getAttribute(CaptchaUtil.getSessionKey());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
