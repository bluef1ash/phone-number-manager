package com.github.phonenumbermanager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.validator.CommunityInputValidator;
import com.github.phonenumbermanager.validator.SubcontractorInputValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 社区控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/community")
@Api(tags = "社区控制器")
public class CommunityController extends BaseController {
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
        boolean isCommunity = !requestUrl.toString().contains("/community/subcontractor")
            && (RequestMethod.POST.toString().equals(request.getMethod())
                || RequestMethod.PUT.toString().equals(request.getMethod()));
        boolean isSubcontractor = requestUrl.toString().contains("/community/subcontractor")
            && (RequestMethod.POST.toString().equals(request.getMethod())
                || RequestMethod.PUT.toString().equals(request.getMethod()));
        if (isCommunity) {
            binder.replaceValidators(new CommunityInputValidator(communityService, request));
        } else if (isSubcontractor) {
            binder.replaceValidators(new SubcontractorInputValidator(subcontractorService, request));
        }
    }

    /**
     * 社区列表及所属街道
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据条数
     * @return 视图页面
     */
    @GetMapping
    @ApiOperation("社区列表及所属街道")
    public Map<String, Object> communityList(@ApiParam(name = "分页页码") Integer page,
        @ApiParam(name = "每页数据条数") Integer limit) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (page == null) {
            jsonMap.put("communities", communityService.getCorrelation());
        } else {
            jsonMap.put("communities", communityService.getCorrelation(page, limit));
        }
        return jsonMap;
    }

    /**
     * 通过社区编号获取
     *
     * @param id
     *            编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ApiOperation("通过社区编号获取")
    public Community getCommunityById(@ApiParam(name = "社区编号", required = true) @PathVariable Long id) {
        return communityService.getCorrelation(id);
    }

    /**
     * 添加、修改社区处理
     *
     * @param request
     *            HTTP请求对象
     * @param community
     *            社区对象
     * @param bindingResult
     *            错误信息对象
     * @return 视图页面
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加、修改社区处理")
    public Map<String, Object> communityCreateOrEditHandle(HttpServletRequest request,
        @ApiParam(name = "社区对象", required = true) @RequestBody @Validated Community community,
        BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (communityService.save(community)) {
                setPhoneNumbers(community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                    String.valueOf(community.getId()));
                phoneNumberService.saveBatch(community.getPhoneNumbers());
            } else {
                throw new JsonException("添加社区失败！");
            }
        } else {
            if (communityService.updateById(community)) {
                setPhoneNumbers(community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                    String.valueOf(community.getId()));
                phoneNumberService.saveOrUpdateBatch(community.getPhoneNumbers());
            } else {
                throw new JsonException("修改社区失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过社区编号删除社区
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ApiOperation("通过社区编号删除社区")
    public Map<String, Object> deleteCommunity(@ApiParam(name = "社区编号", required = true) @PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (!communityService.removeById(id)
            || !phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.COMMUNITY, id)) {
            throw new JsonException("删除社区失败！");
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过街道编号列出社区居委会
     *
     * @param subdistrictId
     *            街道编号
     * @return Ajax消息
     */
    @GetMapping({"/subdistrict", "/subdistrict/{subdistrictId}"})
    @ApiOperation("通过街道编号列出社区居委会")
    public Map<String, Object> getCommunitiesBySubdistrictId(
        @ApiParam(name = "街道编号", required = true) @PathVariable(required = false) Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        getRoleId();
        if (subdistrictId == null) {
            Set<Subdistrict> subdistricts = subdistrictService.getCorrelation(systemCompanyType, communityCompanyType,
                subdistrictCompanyType, systemUser.getLevel(), systemUser.getCompanyId());
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
     * @param data
     *            用户加密数据
     * @param changeType
     *            更改类型
     * @return 是否更改成功
     */
    @PutMapping("/submit")
    @ApiOperation("更改社区是否允许更删改信息")
    public Map<String, Object> chooseSubmitForAjax(
        @ApiParam(name = "用户加密数据", required = true) @RequestParam String data,
        @ApiParam(name = "更改类型", required = true) @RequestParam Integer changeType) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (communityService.update(getDecodeData(data), changeType, communityCompanyType, subdistrictCompanyType)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "上报成功！");
            return jsonMap;
        }
        throw new JsonException("更改失败，请稍后再试！");
    }

    /**
     * 社区分包人列表
     *
     * @param page
     *            分页页码
     * @return 视图页面
     */
    @GetMapping("/subcontractor")
    @ApiOperation("社区分包人列表")
    public Map<String, Object> subcontractorList(@ApiParam(name = "分页页码") Integer page,
        @ApiParam(name = "每页数据条数") Integer limit) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (page == null) {
            jsonMap.put("subcontractors", subcontractorService.get(systemUser.getLevel(), systemUser.getCompanyId(),
                systemCompanyType, communityCompanyType, subdistrictCompanyType));
        } else {
            jsonMap.put("subcontractors", subcontractorService.get(page, limit, systemUser.getLevel(),
                systemUser.getCompanyId(), systemCompanyType, communityCompanyType, subdistrictCompanyType));
        }
        return jsonMap;
    }

    /**
     * 通过编号获取社区分包人
     *
     * @param id
     *            编辑的对应编号
     * @return 社区分包人
     */
    @GetMapping("/subcontractor/{id}")
    @ApiOperation("通过编号获取社区分包人")
    public Map<String, Object>
        getSubcontractorById(@ApiParam(name = "社区分包人编号", required = true) @PathVariable Long id) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(2);
        Subcontractor subcontractor = subcontractorService.getCorrelation(id);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        jsonMap.put("communities", communities);
        jsonMap.put("subcontractor", subcontractor);
        return jsonMap;
    }

    /**
     * 添加、修改社区分包人处理
     *
     * @param request
     *            HTTP请求对象
     * @param subcontractor
     *            社区分包人对象
     * @param bindingResult
     *            错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/subcontractor", method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加、修改社区分包人处理")
    public Map<String, Object> subcontractorCreateOrEditHandle(HttpServletRequest request,
        @ApiParam(name = "社区分包人对象", required = true) @RequestBody @Validated Subcontractor subcontractor,
        BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            getRoleId();
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            jsonMap.put("messageErrors", allErrors);
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (subcontractorService.save(subcontractor)) {
                setPhoneNumbers(subcontractor.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBCONTRACTOR,
                    String.valueOf(subcontractor.getId()));
                phoneNumberService.saveBatch(subcontractor.getPhoneNumbers());
            } else {
                throw new JsonException("添加社区分包人失败！");
            }
        } else {
            if (subcontractorService.updateById(subcontractor)) {
                setPhoneNumbers(subcontractor.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                    String.valueOf(subcontractor.getId()));
                phoneNumberService.saveOrUpdateBatch(subcontractor.getPhoneNumbers());
            } else {
                throw new JsonException("修改社区分包人失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过社区分包人编号删除社区分包人
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/subcontractor/{id}")
    @ApiOperation("通过社区分包人编号删除社区分包人")
    public Map<String, Object> deleteSubcontractor(@ApiParam(name = "社区分包人编号", required = true) @PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (subcontractorService.removeById(id)
            || !phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.SUBCONTRACTOR, id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区分包人成功！");
            return jsonMap;
        }
        throw new JsonException("删除社区分包人失败！");
    }

    /**
     * 通过社区编号加载社区分包人
     *
     * @param communityId
     *            社区编号
     * @return 社区分包人对象集合
     */
    @GetMapping("/subcontractor/community/{communityId}")
    @ApiOperation("通过社区编号加载社区分包人")
    public Map<String, Object>
        loadSubcontractor(@ApiParam(name = "社区编号", required = true) @PathVariable Long communityId) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        List<Subcontractor> subcontractors = subcontractorService.getByCommunityId(communityId);
        jsonMap.put("state", 1);
        jsonMap.put("subcontractors", subcontractors);
        return jsonMap;
    }
}
