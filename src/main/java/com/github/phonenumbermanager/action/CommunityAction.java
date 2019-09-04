package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.validator.CommunityInputValidator;
import com.github.phonenumbermanager.validator.SubcontractorInputValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 社区控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/community")
public class CommunityAction extends BaseAction {
    @Resource
    private CommunityService communityService;
    @Resource
    private SubcontractorService subcontractorService;
    @Resource
    private SubdistrictService subdistrictService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        if (requestUrl.toString().contains("/community/handle")) {
            binder.replaceValidators(new CommunityInputValidator(communityService, request));
        } else if (requestUrl.toString().contains("/subcontractor/handle")) {
            binder.replaceValidators(new SubcontractorInputValidator(subcontractorService, request));
        }
    }

    /**
     * 社区列表
     *
     * @param model 前台模型
     * @param page  分页页码
     * @return 视图页面
     */
    @GetMapping("/list")
    public String communityList(Model model, Integer page) {
        try {
            Map<String, Object> communityMap = communityService.findCorrelation(page, null);
            model.addAttribute("communities", communityMap.get("data"));
            model.addAttribute("pageInfo", communityMap.get("pageInfo"));
            return "community/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加社区
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @GetMapping("/create")
    public String createCommunity(Model model) {
        try {
            List<Subdistrict> subdistricts = subdistrictService.find();
            model.addAttribute("subdistricts", subdistricts);
            return "community/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑社区
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/edit")
    public String editCommunity(Model model, @RequestParam Long id) {
        try {
            Community community = communityService.findCorrelation(id);
            List<Subdistrict> subdistricts = subdistrictService.find();
            model.addAttribute("subdistricts", subdistricts);
            model.addAttribute("community", community);
            return "community/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改社区处理
     *
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param community     社区对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String communityCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Community community, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            try {
                // 输出错误信息
                List<ObjectError> allErrors = bindingResult.getAllErrors();
                model.addAttribute("messageErrors", allErrors);
                List<Subdistrict> subdistricts = subdistrictService.find();
                model.addAttribute("subdistricts", subdistricts);
                return "community/edit";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("展示社区失败！", e);
            }
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            try {
                communityService.create(community);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加社区失败！", e);
            }
        } else {
            try {
                communityService.update(community);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改社区失败！", e);
            }
        }
        return "redirect:/community/list";
    }

    /**
     * 使用AJAX技术通过社区编号删除社区
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteCommunityForAjax(@RequestParam Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            communityService.delete(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区成功！");
            return jsonMap;
        } catch (BusinessException be) {
            be.printStackTrace();
            throw new JsonException(be.getMessage(), be);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除社区失败！", e);
        }
    }

    /**
     * 通过街道编号使用AJAX技术列出社区居委会
     *
     * @param session       Session对象
     * @param subdistrictId 街道级编号
     * @return Ajax消息
     */
    @GetMapping("/ajax_select")
    @ResponseBody
    public Map<String, Object> findCommunitiesForAjax(HttpSession session, Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        getSessionRoleId(session);
        try {
            if (subdistrictId == null) {
                Integer companyType = systemUser.getCompanyType();
                Long companyId = systemUser.getCompanyId();
                Set<Subdistrict> subdistricts = subdistrictService.findCorrelation(systemCompanyType, communityCompanyType, subdistrictCompanyType, companyType, companyId);
                jsonMap.put("subdistricts", subdistricts);
            } else {
                List<Community> communities = communityService.findBySubdistrictId(subdistrictId);
                jsonMap.put("communities", communities);
            }
            jsonMap.put("state", 1);
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("查找社区失败！", e);
        }
    }

    /**
     * 更改社区是否允许更删改信息
     *
     * @param session    Session对象
     * @param data       用户加密数据
     * @param changeType 更改类型
     * @return 是否更改成功
     */
    @PostMapping("/choose_submit")
    @ResponseBody
    public Map<String, Object> chooseSubmitForAjax(HttpSession session, @RequestParam String data, @RequestParam Integer changeType) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            communityService.update(getDecodeData(session, data), changeType, systemCompanyType, communityCompanyType, subdistrictCompanyType);
            jsonMap.put("state", 1);
            jsonMap.put("message", "上报成功！");
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("更改失败，请稍后再试！", e);
        }
    }

    /**
     * 通过社区编号使用Ajax技术加载社区及所属街道
     *
     * @param id 社区编号
     * @return Ajax信息
     */
    @GetMapping("/ajax_load")
    @ResponseBody
    public Map<String, Object> loadCommunityForAjax(@RequestParam Long id) {
        try {
            Community community = communityService.find(id);
            Map<String, Object> jsonMap = new HashMap<>(3);
            jsonMap.put("state", 1);
            jsonMap.put("community", community);
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("加载社区失败！", e);
        }
    }

    /**
     * 社区分包人列表
     *
     * @param session Session对象
     * @param model   前台模型
     * @param page    分页页码
     * @return 视图页面
     */
    @GetMapping("/subcontractor/list")
    public String subcontractorList(HttpSession session, Model model, Integer page) {
        getSessionRoleId(session);
        try {
            Map<String, Object> subcontractorMap = subcontractorService.find(page, null, systemUser.getCompanyType(), systemUser.getCompanyId(), systemCompanyType, communityCompanyType, subdistrictCompanyType);
            model.addAttribute("subcontractors", subcontractorMap.get("data"));
            model.addAttribute("pageInfo", subcontractorMap.get("pageInfo"));
            return "subcontractor/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加社区分包人
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @GetMapping("/subcontractor/create")
    public String createSubcontractor(Model model, HttpSession session) {
        getSessionRoleId(session);
        try {
            List<Community> communities = communityService.find(systemUser, communityCompanyType, subdistrictCompanyType);
            model.addAttribute("communities", JSON.toJSON(communities));
            return "subcontractor/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑社区分包人
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/subcontractor/edit")
    public String editSubcontractor(HttpSession session, Model model, @RequestParam Long id) {
        getSessionRoleId(session);
        try {
            Subcontractor subcontractor = subcontractorService.find(id);
            List<Community> communities = communityService.find(systemUser, communityCompanyType, subdistrictCompanyType);
            model.addAttribute("communities", JSON.toJSON(communities));
            model.addAttribute("communities", communities);
            model.addAttribute("subcontractor", subcontractor);
            return "subcontractor/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改社区分包人处理
     *
     * @param request       HTTP请求对象
     * @param session       Session对象
     * @param model         前台模型
     * @param subcontractor 社区分包人对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/subcontractor/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String subcontractorCreateOrEditHandle(HttpServletRequest request, HttpSession session, Model model, @Validated Subcontractor subcontractor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            try {
                // 输出错误信息
                List<ObjectError> allErrors = bindingResult.getAllErrors();
                model.addAttribute("messageErrors", allErrors);
                List<Community> communities = communityService.find(systemUser, communityCompanyType, subdistrictCompanyType);
                model.addAttribute("communities", JSON.toJSON(communities));
                return "subcontractor/edit";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("展示社区分包人失败！", e);
            }
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            try {
                subcontractorService.create(subcontractor);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加社区分包人失败！", e);
            }
        } else {
            try {
                subcontractorService.update(subcontractor);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改社区分包人失败！", e);
            }
        }
        return "redirect:/community/subcontractor/list";
    }

    /**
     * 使用AJAX技术通过社区分包人编号删除社区分包人
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/subcontractor/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteSubcontractorForAjax(@RequestParam Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            subcontractorService.delete(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区分包人成功！");
            return jsonMap;
        } catch (BusinessException be) {
            be.printStackTrace();
            throw new JsonException(be.getMessage(), be);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除社区分包人失败！", e);
        }
    }

    /**
     * 使用Ajax技术通过社区编号加载社区分包人
     *
     * @param communityId 社区编号
     * @return 社区分包人对象集合
     */
    @GetMapping("/subcontractor/ajax_load")
    @ResponseBody
    public Map<String, Object> loadSubcontractorForAjax(@RequestParam Long communityId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            List<Subcontractor> subcontractors = subcontractorService.findByCommunityId(communityId);
            jsonMap.put("state", 1);
            jsonMap.put("subcontractors", subcontractors);
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("加载社区分包人失败！", e);
        }
    }
}
