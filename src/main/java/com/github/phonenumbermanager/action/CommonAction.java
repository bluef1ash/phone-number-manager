package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.exception.NotfoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共控制器
 *
 * @author 廿二月的天
 */
@Controller
public class CommonAction extends BaseAction {

    /**
     * 通过AJAX技术获取CSRF的Token
     *
     * @param request HTTP请求对象
     * @param data    加密字符串验证是否非法来源
     * @return JSON数据
     */
    @GetMapping("/getcsrf")
    @ResponseBody
    public Map<String, Object> getCsrf(HttpServletRequest request, @RequestParam String data) {
        byte[] jsonDecode = Base64.getDecoder().decode(data);
        Map<String, Object> dataMap = (Map<String, Object>) JSON.parse(jsonDecode);
        String host = request.getHeader("host");
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }
        Map<String, Object> jsonMap = new HashMap<>(3);
        jsonMap.put("state", 0);
        if (host.equals(dataMap.get("host")) && System.currentTimeMillis() - (long) dataMap.get("timeStamp") <= SystemConstant.TIMESTAMP_MILLISECONDS_DIFFERENCE) {
            jsonMap.put("state", 1);
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            jsonMap.put("csrf", csrfToken.getToken());
        }
        return jsonMap;
    }

    /**
     * 403异常页面
     *
     * @throws NotfoundException 404异常
     */
    @GetMapping("/404")
    public String exception() {
        throw new NotfoundException();
    }
}
