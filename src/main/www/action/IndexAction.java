package www.action;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import exception.BusinessException;
import www.entity.UserPrivilege;
import www.service.CommunityResidentService;
import www.service.UserPrivilegeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
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
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "index/index";
    }

    /**
     * 欢迎页
     *
     * @return
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String welcome() {
        return "index/welcome";
    }

    /**
     * 系统简介
     *
     * @return
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about() {
        return "index/about";
    }

    /**
     * 使用AJAX技术获取首页菜单栏内容
     *
     * @param isDisplay
     * @return
     */
    @RequestMapping(value = "/getmenu", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getMenu(Integer isDisplay, HttpSession session) {
        try {
            List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesByIsDisplay(isDisplay, session);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userPrivileges", userPrivileges);
            return map;
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 使用AJAX技术获取图表数据
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/getcomputedcount", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> getComputedCount(HttpSession session) {
        try {
            return communityResidentService.computedCount(session);
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
