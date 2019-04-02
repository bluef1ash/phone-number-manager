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
        model.addAttribute("status", 404);
        model.addAttribute("exception", exception);
        return "exception/default";
    }
}
