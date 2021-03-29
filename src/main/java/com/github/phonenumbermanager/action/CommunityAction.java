package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.PhoneNumberService;
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
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        boolean isCommunity = !requestUrl.toString().contains("/community/subcontractor") && (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod()));
        boolean isSubcontractor = requestUrl.toString().contains("/community/subcontractor") && (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod()));
        if (isCommunity) {
            binder.replaceValidators(new CommunityInputValidator(communityService, request));
        } else if (isSubcontractor) {
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
    @GetMapping({"", "/{page}"})
    public String communityList(Model model, @PathVariable(required = false) Integer page) {
        model.addAttribute("communities", communityService.getCorrelation(page, null));
        return "community/list";
    }

    /**
     * 添加社区
     *
     * @param model 前台模型
     * @return 视图页面
     */
    @GetMapping("/create")
    public String createCommunity(Model model) {
        List<Subdistrict> subdistricts = subdistrictService.list();
        model.addAttribute("subdistricts", subdistricts);
        return "community/edit";
    }

    /**
     * 编辑社区
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/edit/{id}")
    public String editCommunity(Model model, @PathVariable Long id) {
        Community community = communityService.getCorrelation(id);
        List<Subdistrict> subdistricts = subdistrictService.list();
        model.addAttribute("subdistricts", subdistricts);
        model.addAttribute("community", community);
        return "community/edit";
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
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public String communityCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Community community, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            List<Subdistrict> subdistricts = subdistrictService.list();
            model.addAttribute("subdistricts", subdistricts);
            return "community/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (communityService.save(community)) {
                setPhoneNumbers(community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY, String.valueOf(community.getId()));
                phoneNumberService.saveBatch(community.getPhoneNumbers());
            } else {
                throw new BusinessException("添加社区失败！");
            }
        } else {
            if (communityService.updateById(community)) {
                setPhoneNumbers(community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY, String.valueOf(community.getId()));
                phoneNumberService.saveOrUpdateBatch(community.getPhoneNumbers());
            } else {
                throw new BusinessException("修改社区失败！");
            }
        }
        return "redirect:/community";
    }

    /**
     * 使用AJAX技术通过社区编号删除社区
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, Object> deleteCommunityForAjax(@PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (!communityService.removeById(id) || !phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.COMMUNITY, id)) {
            throw new JsonException("删除社区失败！");
        }
        jsonMap.put("state", 1);
        jsonMap.put("message", "删除社区成功！");
        return jsonMap;
    }

    /**
     * 通过街道编号使用AJAX技术列出社区居委会
     *
     * @param session       Session对象
     * @param subdistrictId 街道级编号
     * @return Ajax消息
     */
    @GetMapping({"/select", "/select/{subdistrictId}"})
    @ResponseBody
    public Map<String, Object> findCommunitiesForAjax(HttpSession session, @PathVariable(required = false) Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        getSessionRoleId(session);
        if (subdistrictId == null) {
            Set<Subdistrict> subdistricts = subdistrictService.getCorrelation(systemCompanyType, communityCompanyType, subdistrictCompanyType, systemUser.getLevel(), systemUser.getCompanyId());
            jsonMap.put("subdistricts", subdistricts);
        } else {
            List<Community> communities = communityService.getBySubdistrictId(subdistrictId);
            jsonMap.put("communities", communities);
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 更改社区是否允许更删改信息
     *
     * @param session    Session对象
     * @param data       用户加密数据
     * @param changeType 更改类型
     * @return 是否更改成功
     */
    @PutMapping("/submit")
    @ResponseBody
    public Map<String, Object> chooseSubmitForAjax(HttpSession session, @RequestParam String data, @RequestParam Integer changeType) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (communityService.update(getDecodeData(session, data), changeType, communityCompanyType, subdistrictCompanyType)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "上报成功！");
            return jsonMap;
        }
        throw new JsonException("更改失败，请稍后再试！");
    }

    /**
     * 通过社区编号使用Ajax技术加载社区及所属街道
     *
     * @param id 社区编号
     * @return Ajax信息
     */
    @GetMapping("/load/{id}")
    @ResponseBody
    public Map<String, Object> loadCommunityForAjax(@PathVariable Long id) {
        Community community = communityService.getById(id);
        Map<String, Object> jsonMap = new HashMap<>(3);
        jsonMap.put("state", 1);
        jsonMap.put("community", community);
        return jsonMap;
    }

    /**
     * 社区分包人列表
     *
     * @param session Session对象
     * @param model   前台模型
     * @param page    分页页码
     * @return 视图页面
     */
    @GetMapping({"/subcontractor", "/subcontractor/{page}"})
    public String subcontractorList(HttpSession session, Model model, @PathVariable(required = false) Integer page) {
        getSessionRoleId(session);
        model.addAttribute("subcontractors", subcontractorService.get(page, null, systemUser.getLevel(), systemUser.getCompanyId(), systemCompanyType, communityCompanyType, subdistrictCompanyType));
        return "subcontractor/list";
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
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", JSON.toJSON(communities));
        return "subcontractor/edit";
    }

    /**
     * 编辑社区分包人
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/subcontractor/edit/{id}")
    public String editSubcontractor(HttpSession session, Model model, @PathVariable Long id) {
        getSessionRoleId(session);
        Subcontractor subcontractor = subcontractorService.getCorrelation(id);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", JSON.toJSON(communities));
        model.addAttribute("communities", communities);
        model.addAttribute("subcontractor", subcontractor);
        return "subcontractor/edit";
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
    @RequestMapping(value = "/subcontractor", method = {RequestMethod.POST, RequestMethod.PUT})
    public String subcontractorCreateOrEditHandle(HttpServletRequest request, HttpSession session, Model model, @Validated Subcontractor subcontractor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
            model.addAttribute("communities", JSON.toJSON(communities));
            return "subcontractor/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (subcontractorService.save(subcontractor)) {
                setPhoneNumbers(subcontractor.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBCONTRACTOR, String.valueOf(subcontractor.getId()));
                phoneNumberService.saveBatch(subcontractor.getPhoneNumbers());
            } else {
                throw new BusinessException("添加社区分包人失败！");
            }
        } else {
            if (subcontractorService.updateById(subcontractor)) {
                setPhoneNumbers(subcontractor.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY, String.valueOf(subcontractor.getId()));
                phoneNumberService.saveOrUpdateBatch(subcontractor.getPhoneNumbers());
            } else {
                throw new BusinessException("修改社区分包人失败！");
            }
        }
        return "redirect:/community/subcontractor";
    }

    /**
     * 使用AJAX技术通过社区分包人编号删除社区分包人
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/subcontractor/{id}")
    @ResponseBody
    public Map<String, Object> deleteSubcontractorForAjax(@PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (subcontractorService.removeById(id) || !phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.SUBCONTRACTOR, id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区分包人成功！");
            return jsonMap;
        }
        throw new JsonException("删除社区分包人失败！");
    }

    /**
     * 使用Ajax技术通过社区编号加载社区分包人
     *
     * @param communityId 社区编号
     * @return 社区分包人对象集合
     */
    @GetMapping("/subcontractor/load/{communityId}")
    @ResponseBody
    public Map<String, Object> loadSubcontractorForAjax(@PathVariable Long communityId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        List<Subcontractor> subcontractors = subcontractorService.getByCommunityId(communityId);
        jsonMap.put("state", 1);
        jsonMap.put("subcontractors", subcontractors);
        return jsonMap;
    }
}
