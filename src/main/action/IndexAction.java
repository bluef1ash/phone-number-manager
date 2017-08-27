package main.action;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import exception.BusinessException;
import main.entity.UserPrivilege;
import main.service.UserPrivilegeService;
/**
 * 首页控制器
 *
 */
@Controller
public class IndexAction {
	@Resource
	private UserPrivilegeService userPrivilegeService;
	/**
	 * 登录后首页
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index() {
		return "index/index";
	}
	/**
	 * 欢迎页
	 * @return
	 */
	@RequestMapping(value = "/index/welcome", method = RequestMethod.GET)
	public String welcome() {
		return "index/welcome";
	}
	/**
	 * 系统简介
	 * @return
	 */
	@RequestMapping(value = "/index/about", method = RequestMethod.GET)
	public String about() {
		return "index/about";
	}
	/**
	 * 使用AJAX技术获取首页菜单栏内容
	 * @param isDisplay
	 * @return
	 */
	@RequestMapping(value = "/index/getmenu", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMenu(Integer isDisplay, HttpSession session) {
		Map<String, Object> map = null;
		try {
			List<UserPrivilege> userPrivileges = userPrivilegeService.findPrivilegesByIsDisplay(isDisplay, session);
			map = new HashMap<String, Object>();
			map.put("userPrivileges", userPrivileges);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return map;
	}
}