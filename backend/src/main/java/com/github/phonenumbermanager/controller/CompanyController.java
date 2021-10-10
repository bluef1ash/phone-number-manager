package com.github.phonenumbermanager.controller;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 单位控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/company")
@Api(tags = "单位控制器")
public class CompanyController extends BaseController {
    @Resource
    private CompanyService companyService;
    @Resource
    private SubcontractorService subcontractorService;
    @Resource
    private PhoneNumberService phoneNumberService;

    /**
     * 单位列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据条数
     * @return 视图页面
     */
    @GetMapping
    @ApiOperation("单位列表")
    public R companyList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据条数") Integer limit) {
        Object companies;
        if (page == null) {
            companies = companyService.getCorrelation();
        } else {
            companies = companyService.getCorrelation(page, limit);
        }
        return R.ok().put("companies", companies);
    }

    /**
     * 通过单位编号获取
     *
     * @param id
     *            编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ApiOperation("通过单位编号获取")
    public R getCompanyById(@ApiParam(name = "需要编辑的单位编号", required = true) @PathVariable Long id) {
        return R.ok().put("companies", companyService.getCorrelation(id));
    }

    /**
     * 单位添加处理
     *
     * @param company
     *            单位对象
     * @return 视图页面
     */
    @PostMapping
    @ApiOperation("单位添加处理")
    public R companyCreateHandle(@ApiParam(name = "需要添加的单位对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Company company) {
        if (companyService.save(company)) {
            return setPhoneNumbers(phoneNumberService, company.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                String.valueOf(company.getId()));
        }
        throw new JsonException("添加单位失败！");
    }

    /**
     * 单位修改处理
     *
     * @param company
     *            单位对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("单位修改处理")
    public R companyModifyHandle(@ApiParam(name = "需要修改的单位对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) Company company) {
        if (companyService.updateById(company)) {
            return setPhoneNumbers(phoneNumberService, company.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY,
                String.valueOf(company.getId()));
        }
        throw new JsonException("单位修改失败！");
    }

    /**
     * 通过单位编号删除
     *
     * @param id
     *            需要删除的单位编号
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @ApiOperation("通过单位编号删除")
    public R deleteCompany(@ApiParam(name = "需要删除的单位编号", required = true) @PathVariable Long id) {
        if (!companyService.removeCorrelationById(id)) {
            return R.error(ExceptionCode.DELETE_FAILED.getCode(), "删除社区失败！");
        }
        return R.ok("删除社区成功！");
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
