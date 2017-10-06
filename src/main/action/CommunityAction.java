package main.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import annotation.SystemUserAuth;
import exception.BusinessException;
import main.entity.Community;
import main.entity.Subdistrict;
import main.service.CommunityService;
import main.service.SubdistrictService;

/**
 * 社区控制器
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
     *
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String communityList(Model model, Integer page) {
        try {
            Map<String, Object> communityMap = communityService.findCommunitiesAndSubdistrict(page, null);
            model.addAttribute("communities", communityMap.get("data"));
            System.out.println(communityMap.get("data"));
            model.addAttribute("pageInfo", communityMap.get("pageInfo"));
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "community/list";
    }

    /**
     * 添加社区
     *
     * @param model
     * @return
     */
    @RefreshCsrfToken
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
     *
     * @param model
     * @param id
     * @return
     */
    @RefreshCsrfToken
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
     *
     * @param request
     * @param model
     * @param community
     * @param bindingResult
     * @return
     */
    @VerifyCSRFToken
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String communityCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Community community, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "community/edit";
        }
        if ("POST".equals(request.getMethod())) {
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
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteCommunityForAjax(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            communityService.deleteObjectById(id);
            map.put("state", 1);
            map.put("message", "删除社区成功！");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", 0);
            map.put("message", "删除社区失败！");
        }
        return map;
    }
}
