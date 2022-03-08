package com.github.phonenumbermanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 单位控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/company")
@Api(tags = "单位控制器")
public class CompanyController extends BaseController {
    private final CompanyService companyService;

    /**
     * 单位列表
     *
     * @param request
     *            HTTP请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据条数
     * @return 视图页面
     */
    @GetMapping
    @ApiOperation("单位列表")
    public R companyList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据条数") Integer pageSize) {
        getEnvironmentVariable();
        List<Company> companies = systemUser.getCompanies();
        if (companies == null
            && systemUser.getId().equals(configurationMap.get("system_administrator_id").get("content"))) {
            companies = companyService.list();
        }
        QueryWrapper<Configuration> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", companyService.pageCorrelation(companies, current, pageSize, search, sort));
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
        return R.ok().put("data", companyService.getCorrelation(id));
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
        if (!companyService.removeById(id)) {
            throw new JsonException("删除社区失败！");
        }
        return R.ok("删除社区成功！");
    }

    /**
     * 单位表单列表
     *
     * @return 单位表单列表JSON
     */
    @GetMapping("/select-list")
    @ApiOperation("单位表单列表")
    public R companySelectList() {
        return R.ok().put("data", companyService.treeSelectList());
    }

    /**
     * 增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/batch")
    @ApiOperation("增删改批量操作")
    public R companyBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            if (companyService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }
}
