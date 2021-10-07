package com.github.phonenumbermanager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
    public R communityList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据条数") Integer limit) {
        if (page == null) {
            return R.ok().put("communities", communityService.getCorrelation());
        } else {
            return R.ok().put("communities", communityService.getCorrelation(page, limit));
        }
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
    public R getCommunityById(@ApiParam(name = "社区编号", required = true) @PathVariable Long id) {
        return R.ok().put("communities", communityService.getCorrelation(id));
    }

    /**
     * 添加处理
     *
     * @param community
     *            社区对象
     * @return 视图页面
     */
    @PostMapping
    @ApiOperation("添加社区处理")
    public R communityCreateHandle(
        @ApiParam(name = "社区对象", required = true) @RequestBody @Validated(CreateInputGroup.class) Community community) {
        if (communityService.save(community)) {
            return setPhoneNumbers(phoneNumberService, community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                String.valueOf(community.getId()));
        }
        throw new JsonException("添加社区失败！");
    }

    /**
     * 修改社区处理
     *
     * @param community
     *            社区对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("修改社区处理")
    public R communityModifyHandle(
        @ApiParam(name = "社区对象", required = true) @RequestBody @Validated(ModifyInputGroup.class) Community community) {
        if (communityService.updateById(community)) {
            return setPhoneNumbers(phoneNumberService, community.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                String.valueOf(community.getId()));
        }
        throw new JsonException("修改社区失败！");
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
        if (!communityService.removeCorrelationById(id)) {
            return R.error(ExceptionCode.DELETE_FAILED.getCode(), "删除社区失败！");
        }
        return R.ok("删除社区成功！");
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
    public R getCommunitiesBySubdistrictId(
        @ApiParam(name = "街道编号", required = true) @PathVariable(required = false) Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        getRoleId();
        if (subdistrictId == null) {
            //// TODO: 2021/9/12 0012 用户权限
            // Set<Subdistrict> subdistricts = subdistrictService.getCorrelation(systemCompanyType,
            //// communityCompanyType,
            // subdistrictCompanyType, systemUser.getLevel(), systemUser.getCompanyId());
            // jsonMap.put("subdistricts", subdistricts);
        } else {
            List<Community> communities = communityService.getBySubdistrictId(subdistrictId);
            jsonMap.put("communities", communities);
        }
        return R.ok(jsonMap);
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
    public R chooseSubmitForAjax(@ApiParam(name = "用户加密数据", required = true) @RequestParam String data,
        @ApiParam(name = "更改类型", required = true) @RequestParam Integer changeType) {
        if (communityService.update(getDecodeData(data), changeType, communityCompanyType, subdistrictCompanyType)) {
            return R.ok("上报成功！");
        }
        return R.error(ExceptionCode.EDIT_FAILED.getCode(), "更改失败，请稍后再试！");
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
    public R subcontractorList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据条数") Integer limit) {
        getRoleId();
        // // TODO: 2021/9/12 0012
        /*if (page == null) {
            jsonMap.put("subcontractors", subcontractorService.get(systemUser.getLevel(), systemUser.getCompanyId(),
                systemCompanyType, communityCompanyType, subdistrictCompanyType));
        } else {
            jsonMap.put("subcontractors", subcontractorService.get(page, limit, systemUser.getLevel(),
                systemUser.getCompanyId(), systemCompanyType, communityCompanyType, subdistrictCompanyType));
        }*/
        return R.ok();
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
    public R getSubcontractorById(@ApiParam(name = "社区分包人编号", required = true) @PathVariable Long id) {
        getRoleId();
        Subcontractor subcontractor = subcontractorService.getCorrelation(id);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        return Objects.requireNonNull(R.ok().put("communities", communities)).put("subcontractor", subcontractor);
    }

    /**
     * 添加社区分包人处理
     *
     * @param subcontractor
     *            社区分包人对象
     * @return 视图页面
     */
    @PostMapping("/subcontractor")
    @ApiOperation("添加社区分包人处理")
    public R subcontractorCreateHandle(@ApiParam(name = "社区分包人对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Subcontractor subcontractor) {
        if (subcontractorService.save(subcontractor)) {
            return setPhoneNumbers(phoneNumberService, subcontractor.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.SUBCONTRACTOR, String.valueOf(subcontractor.getId()));
        }
        throw new JsonException("添加社区分包人失败！");
    }

    /**
     * 修改社区分包人处理
     *
     * @param subcontractor
     *            社区分包人对象
     * @return 视图页面
     */
    @PutMapping("/subcontractor")
    @ApiOperation("修改社区分包人处理")
    public R subcontractorModifyHandle(@ApiParam(name = "社区分包人对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) Subcontractor subcontractor) {
        if (subcontractorService.updateById(subcontractor)) {
            return setPhoneNumbers(phoneNumberService, subcontractor.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.COMMUNITY, String.valueOf(subcontractor.getId()));
        }
        throw new JsonException("修改社区分包人失败！");
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
    public R deleteSubcontractor(@ApiParam(name = "社区分包人编号", required = true) @PathVariable Long id) {
        if (!subcontractorService.removeCorrelationById(id)) {
            return R.ok("删除社区分包人成功！");
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
    public R loadSubcontractor(@ApiParam(name = "社区编号", required = true) @PathVariable Long communityId) {
        List<Subcontractor> subcontractors = subcontractorService.getByCommunityId(communityId);
        return R.ok().put("subcontractors", subcontractors);
    }
}
