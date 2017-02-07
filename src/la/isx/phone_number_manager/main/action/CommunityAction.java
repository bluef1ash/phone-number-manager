package la.isx.phone_number_manager.main.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import la.isx.phone_number_manager.annotation.SystemUserAuth;
import la.isx.phone_number_manager.exception.BusinessException;
import la.isx.phone_number_manager.main.entity.Community;
import la.isx.phone_number_manager.main.entity.Subdistrict;
import la.isx.phone_number_manager.main.service.CommunityService;
import la.isx.phone_number_manager.main.service.SubdistrictService;

/**
 * 社区控制器
 *
 */
@SystemUserAuth
@Controller
@RequestMapping("/community")
public class CommunityAction {
	@Resource
	private CommunityService communityService;
	@Resource
	private SubdistrictService subdistrictService;
	/**
	 * 社区列表
	 * @param model
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String communityList(Model model, Integer page) {
		try {
			Map<String, Object> communityMap = communityService.findObjects(page, null);
			model.addAttribute("communities", communityMap.get("data"));
			model.addAttribute("pageInfo", communityMap.get("pageInfo"));
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "community/list";
	}
	/**
	 * 添加社区
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createCommunity(Model model) {
		try {
			List<Subdistrict> subdistricts = subdistrictService.findObjects();
			model.addAttribute("subdistricts", subdistricts);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "community/create";
	}
	/**
	 * 编辑社区
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editCommunity(Model model, Integer id) {
		try {
			Community community = communityService.findCommunityAndSubdistrictById(id);
			List<Subdistrict> subdistricts = subdistrictService.findObjects();
			model.addAttribute("subdistricts", subdistricts);
			model.addAttribute("community", community);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "community/edit";
	}
	/**
	 * 添加、修改社区处理
	 * @param model
	 * @param subdistrict
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/handle", method = RequestMethod.POST)
	public String communityCreateOrEditHandle(Model model, @Validated Community community, BindingResult bindingResult) {
		if (community.getCommunityId() == null) {
			if (bindingResult.hasErrors()) {
				// 输出错误信息
				List<ObjectError> allErrors = bindingResult.getAllErrors();
				model.addAttribute("messageErrors", allErrors);
				return "community/edit";
			}
			try {
				communityService.createObject(community);
			} catch (Exception e) {
				throw new BusinessException("添加社区失败！", e);
			}
		} else {
			try {
				communityService.updateObject(community);
			} catch (Exception e) {
				throw new BusinessException("修改社区失败！", e);
			}
		}
		return "redirect:/community/list.action";
	}
	/**
	 * 使用AJAX技术通过社区ID删除社区
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/ajax_delete", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteCommunityForAjax(Integer id) {
		Map<String, Object> map = null;
		try {
			communityService.deleteObjectById(id);
			map = new HashMap<String, Object>();
			map.put("message", 1);
			return map;
		} catch (Exception e) {
			throw new BusinessException("删除社区失败！", e);
		}
	}
}