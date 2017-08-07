package main.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import annotation.RefreshCsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import annotation.SystemUserAuth;
import exception.BusinessException;
import exception.ParameterException;
import main.entity.Community;
import main.entity.CommunityResident;
import main.service.CommunityResidentService;
import main.service.CommunityService;

/**
 * 社区居民控制器
 *
 */
@SystemUserAuth
@Controller
@RequestMapping("/resident")
public class CommunityResidentAction {
	@Resource
	private CommunityResidentService communityResidentService;
	@Resource
	private CommunityService communityService;
	/**
	 * 社区居民列表
	 * @param model
	 * @param page
	 * @param communityResident
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
	public String communityResidentList(Model model, Integer page, CommunityResident communityResident) {
		try {
			Map<String, Object> communityResidentMap = null;
			if (communityResident == null) {
				communityResidentMap = communityResidentService.findObjects(page, null);
			} else {
				communityResidentMap = communityResidentService.findObjects(communityResident, page, null);
			}
			model.addAttribute("communityResidents", communityResidentMap.get("data"));
			model.addAttribute("pageInfo", communityResidentMap.get("pageInfo"));
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "resident/list";
	}
	/**
	 * 添加社区居民
	 * @param model
	 * @return
	 */
	@RefreshCsrfToken
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createCommunityResident(Model model) {
		try {
			List<Community> communities = communityService.findObjects();
			model.addAttribute("communities", communities);
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "resident/create";
	}
	/**
	 * 编辑社区居民
	 * @param model
	 * @param id
	 * @return
	 */
    @RefreshCsrfToken
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editCommunityResident(Model model, Integer id) {
		try {
			Map<String, Object> communityResidentMap = communityResidentService.findCommunityResidentAndCommunityById(id);
			List<Community> communities = communityService.findObjects();
			model.addAttribute("communities", communities);
			model.addAttribute("communityResident", communityResidentMap.get("communityResident"));
			model.addAttribute("residentPhones", communityResidentMap.get("residentPhones"));
		} catch (Exception e) {
			throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
		}
		return "resident/edit";
	}
	/**
	 * 添加、修改社区居民处理
	 * @param model
	 * @param communityResident
	 * @param residentPhones
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/handle", method = RequestMethod.POST)
	public String communityResidentCreateOrEditHandle(Model model, @Validated CommunityResident communityResident, @RequestParam(value = "residentPhones[]")  String[] residentPhones, BindingResult bindingResult) {
		if (residentPhones.length > 1) {
			if (communityResident.getCommunityResidentId() == null) {
				if (bindingResult.hasErrors()) {
					// 输出错误信息
					List<ObjectError> allErrors = bindingResult.getAllErrors();
					model.addAttribute("messageErrors", allErrors);
					return "resident/edit";
				}
				try {
					communityResidentService.createCommuntyResident(communityResident, residentPhones);
				} catch (Exception e) {
					throw new BusinessException("添加社区居民失败！", e);
				}
			} else {
				try {
					communityResidentService.updateCommuntyResident(communityResident, residentPhones);
				} catch (Exception e) {
					throw new BusinessException("修改社区居民失败！", e);
				}
			}
		} else {
			throw new ParameterException("社区居民联系方式必须填写一项！");
		}
		return "redirect:/resident/list.action";
	}
	/**
	 * 使用AJAX技术通过社区居民ID删除社区居民
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/ajax_delete", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteCommunityResidentForAjax(Integer id) {
		Map<String, Object> map = null;
		try {
			communityResidentService.deleteObjectById(id);
			map = new HashMap<String, Object>();
			map.put("message", 1);
			return map;
		} catch (Exception e) {
			throw new BusinessException("删除社区居民失败！", e);
		}
	}
	/**
	 * 导入居民信息进系统
	 * @param session
	 * @return
	 */
	@SystemUserAuth(enforce = true)
	@RequestMapping(value = "/import_as_system", method = RequestMethod.GET)
	public String communityResidentImportAsSystem(HttpSession session) {
		
		return "redirect:/resident/list.action";
	}
	/**
	 * 导出居民信息到Excel
	 * @param session
	 */
	@RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
	public void communityResidentSaveAsExcel(HttpSession session) {
		
	}
}