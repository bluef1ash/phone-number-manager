package la.isx.phone_number_manager.main.action;

import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import la.isx.phone_number_manager.exception.BusinessException;
import la.isx.phone_number_manager.main.entity.SystemUser;
import la.isx.phone_number_manager.main.service.SystemUserService;
/**
 * 登录控制器
 *
 */
@Controller
@RequestMapping("/login")
public class LoginAction {
	@Resource
	private SystemUserService systemUserService;
	/**
	 * 通过AJAX验证用户能否登录
	 * @param systemUser 异步传输的用户实体对象
	 * @return JSON格式信息
	 */
	@RequestMapping(value = "/ajax", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> ajaxLogin(HttpServletRequest request, HttpSession session, SystemUser systemUser) {
		Map<String, Object> map = null;
		if (systemUser != null) {
			try {
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
}