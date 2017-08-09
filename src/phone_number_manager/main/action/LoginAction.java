package main.action;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.entity.Captcha;
import main.entity.Community;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import exception.BusinessException;
import main.entity.SystemUser;
import main.service.SystemUserService;

/**
 * 登录控制器
 */
@Controller
@RequestMapping("/login")
public class LoginAction {
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private Captcha captcha;
    private static String sRand;

    /**
     * 通过AJAX验证用户能否登录
     *
     * @param systemUser 异步传输的用户实体对象
     * @return JSON格式信息
     */
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> ajaxLogin(HttpServletRequest request, HttpSession session, SystemUser systemUser) {
        Map<String, Object> map = null;
        if (systemUser != null) {
            try {
                if(msg==null&& msg.isEmpty()){
                    //得到用户读入框信息，如果没有输入或者为空，直接跳转到验证失败页面
                    return "error";
                }else{
                    if(sRand.equalsIgnoreCase((msg))){
                        //得到用户输入的验证码匹配成功，跳转到验证通过页面
                        return "ok";
                    }else{
                        //得到用户输入的验证码匹配失败，跳转到验证失败页面
                        return "error";
                    }
                }
                map = systemUserService.loginCheck(request, systemUser);
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
            return "redirect:/jsp/login/login.jsp";
        }
        return "redirect:/index.action";
    }

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
            ServletOutputStream sos = null;
            sos = response.getOutputStream();
            ImageIO.write(captcha.getImage(request), "jpeg", sos);
            sos.close();
            sRand = (String) request.getAttribute("sRand");
            String result = "ok";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}