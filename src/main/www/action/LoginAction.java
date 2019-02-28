package www.action;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import constant.SystemConstant;
import exception.JsonException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CommonUtil;
import utils.GeetestLibUtil;
import www.entity.SystemUser;
import www.service.SystemUserService;
import www.validator.LoginInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("ajaxLogin".equals(validFunction)) {
            binder.replaceValidators(new LoginInputValidator(systemUserService, request));
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
     * @param session       session对象
     * @param systemUser    异步传输的用户实体对象
     * @param bindingResult 错误信息对象
     * @return JSON格式信息
     */
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> ajaxLogin(HttpSession session, @Validated SystemUser systemUser, BindingResult bindingResult) {
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
                    session.setAttribute("userPrivileges", map.get("userPrivileges"));
                    session.setAttribute("configurationsMap", map.get("configurationsMap"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsonException("登录失败，请稍后再试！", e);
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
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        if (systemUser != null) {
            session.invalidate();
            return "redirect:/login";
        }
        return "redirect:/index";
    }

    /**
     * 生成图案验证码数据
     *
     * @param session     Session对象
     * @param browserType 浏览器类型
     * @return 验证图案数据
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject captcha(HttpSession session, String browserType) {
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", browserType);
        param.put("ip_address", CommonUtil.getIp(request));
        int gtServerStatus = gtSdk.preProcess(param);
        session.setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        return JSON.parseObject(gtSdk.getResponseStr());
    }
}
