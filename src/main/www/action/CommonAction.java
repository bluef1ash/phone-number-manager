package www.action;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

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
     * 404页面
     *
     * @param model 前台模型
     * @return 404页面
     */
    @RequestMapping("/*")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noHandlerFound(Model model) {
        Map<String, String> exception = new HashMap<>(2);
        exception.put("message", "找不到此页面！");
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("exception", exception);
        return "exception/default";
    }

    /**
     * 权限拒绝异常页面
     *
     * @param model 前台模型
     * @return 异常页面
     */
    @RequestMapping("/permissiondenied")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String permissionDenied(Model model) {
        Map<String, String> exception = new HashMap<>(2);
        exception.put("message", "您没有该权限，请联系上级管理用户！");
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("exception", exception);
        return "exception/default";
    }
}
