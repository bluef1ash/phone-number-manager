package www.action;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import exception.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import www.entity.SystemUser;
import www.entity.UserPrivilege;
import www.service.CommunityResidentService;
import www.service.UserPrivilegeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 首页控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/index")
public class IndexAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private UserPrivilegeService userPrivilegeService;

    /**
     * 登录后首页
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpSession session, Model model) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        model.addAttribute("systemUser", systemUser);
        return "index/index";
    }

    /**
     * 欢迎页
     *
     * @return 视图页面
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String welcome() {
        return "index/welcome";
    }

    /**
     * 系统简介
     *
     * @return 视图页面
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about() {
        return "index/about";
    }

    /**
     * 使用AJAX技术获取首页菜单栏内容
     *
     * @param isDisplay 是否显示
     * @return 视图页面
     */
    @RequestMapping(value = "/getmenu", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getMenu(Integer isDisplay, HttpSession session) {
        Set<UserPrivilege> userPrivileges = (Set<UserPrivilege>) session.getAttribute("userPrivileges");
        try {
            Map<String, Object> map = new HashMap<>(2);
            map.put("userPrivileges", userPrivilegeService.findPrivilegesByIsDisplay(isDisplay, userPrivileges));
            return map;
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 使用AJAX技术获取图表数据
     *
     * @param session session对象
     * @return 视图页面
     */
    @RequestMapping(value = "/getcomputedcount", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> getComputedCount(HttpSession session) {
        try {
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            return communityResidentService.computedCount(systemUser);
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
