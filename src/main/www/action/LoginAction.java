package www.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import constant.SystemConstant;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CommonUtil;
import utils.GeetestLibUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/login")
public class LoginAction extends BaseAction {

    /**
     * 登录页面
     *
     * @return 视图页面
     */
    @RequestMapping(method = RequestMethod.GET)
    public String login() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            return "redirect:/";
        }
        return "login/login";
    }

    /**
     * 生成图案验证码数据
     *
     * @param request     HTTP请求对象
     * @param browserType 浏览器类型
     * @return 验证图案数据
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject captcha(HttpServletRequest request, String browserType) {
        GeetestLibUtil gtSdk = new GeetestLibUtil(SystemConstant.GEETEST_ID, SystemConstant.GEETEST_KEY, false);
        Map<String, String> param = new HashMap<>(3);
        param.put("client_type", browserType);
        param.put("ip_address", CommonUtil.getIp(request));
        int gtServerStatus = gtSdk.preProcess(param);
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        return JSON.parseObject(gtSdk.getResponseStr());
    }
}
