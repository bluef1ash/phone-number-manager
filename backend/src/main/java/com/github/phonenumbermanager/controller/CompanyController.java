package com.github.phonenumbermanager.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
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
        getEnvironmentVariable();
        return R.ok().put("companies", companyService.pageCorrelation(systemUser.getCompanies(), page, limit));
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
            return R.ok();
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
            return R.ok();
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
    public R removeCompany(@ApiParam(name = "需要删除的单位编号", required = true) @PathVariable Long id) {
        if (!companyService.removeCorrelationById(id)) {
            throw new JsonException("删除社区失败！");
        }
        return R.ok("删除社区成功！");
    }
}
