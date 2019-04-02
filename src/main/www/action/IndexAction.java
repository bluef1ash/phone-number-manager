package www.action;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import exception.JsonException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CsrfTokenUtil;
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
public class IndexAction extends BaseAction {
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
    @RefreshCsrfToken
    public String index(HttpSession session, Model model) {
        getSessionRoleId(session);
        model.addAttribute("systemUser", systemUser);
        model.addAttribute("systemRoleId", systemRoleId);
        model.addAttribute("communityRoleId", communityRoleId);
        model.addAttribute("subdistrictRoleId", subdistrictRoleId);
        return "index/index";
    }

    /**
     * 使用AJAX技术获取首页菜单栏内容
     *
     * @param isDisplay 是否显示
     * @return 视图页面
     */
    @RequestMapping(value = "/getmenu", method = RequestMethod.GET)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> getMenu(Integer isDisplay, HttpSession session) {
        Set<UserPrivilege> userPrivileges = (Set<UserPrivilege>) session.getAttribute("userPrivileges");
        try {
            Map<String, Object> jsonMap = new HashMap<>(4);
            jsonMap.put("state", 1);
            jsonMap.put("userPrivileges", userPrivilegeService.findPrivilegesByIsDisplay(isDisplay, userPrivileges));
            jsonMap.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return jsonMap;
        } catch (Exception e) {
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 使用AJAX技术获取图表数据
     *
     * @param session     session对象
     * @param getType     需要获取的类型，null全部，1基本信息，2柱状图
     * @param id          需要获取的单位编号
     * @param companyType 需要获取的单位类型
     * @return Ajax返回JSON对象
     */
    @RequestMapping(value = "/getcomputedcount", method = RequestMethod.GET)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> getComputedCount(HttpSession session, Integer getType, Long id, Long companyType) {
        getSessionRoleId(session);
        try {
            Map<String, Object> jsonMap = communityResidentService.computedCount(systemUser, getType, id, companyType, systemRoleId, communityRoleId, subdistrictRoleId);
            jsonMap.put("state", 1);
            jsonMap.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
