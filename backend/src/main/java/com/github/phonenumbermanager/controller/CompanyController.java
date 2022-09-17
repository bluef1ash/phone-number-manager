package com.github.phonenumbermanager.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;
import com.github.phonenumbermanager.vo.ComputedVo;

import cn.hutool.core.convert.Convert;
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
    private final ConfigurationService configurationService;
    private final SubcontractorService subcontractorService;

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
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = getUserCompanies(configurationService.mapAll(), currentSystemUser, companyService);
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
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
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (companyService.save(company, currentSystemUser.getId())) {
            return R.ok();
        }
        throw new JsonException("添加单位失败！");
    }

    /**
     * 单位修改处理
     *
     * @param id
     *            要修改的单位编号
     * @param company
     *            单位对象
     * @return 视图页面
     */
    @PutMapping("/{id}")
    @ApiOperation("单位修改处理")
    public R companyModifyHandle(@ApiParam(name = "要修改的单位编号", required = true) @PathVariable Long id,
        @ApiParam(name = "需要修改的单位对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) Company company) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company one = companyService.getOne(new LambdaQueryWrapper<Company>().eq(Company::getId, id));
        company.setId(id).setVersion(one.getVersion());
        if (companyService.updateById(company, currentSystemUser.getId())) {
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
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (companyService.removeById(id, currentSystemUser.getId())) {
            return R.ok("删除社区成功！");
        }
        throw new JsonException("删除社区失败！");
    }

    /**
     * 单位表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 单位表单列表JSON
     */
    @GetMapping("/select-list")
    @ApiOperation("单位表单列表")
    public R companySelectList(Long[] parentIds) {
        return R.ok().put("data", companyService.treeSelectList(parentIds));
    }

    /**
     * 单位增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/batch")
    @ApiOperation("单位增删改批量操作")
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

    /**
     * 社区分包人员列表
     *
     * @param request
     *            HTTP请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据条数
     * @return 视图页面
     */
    @GetMapping("/subcontractor")
    @ApiOperation("社区分包人员列表")
    public R subcontractorList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据条数") Integer pageSize) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = getUserCompanies(configurationService.mapAll(), currentSystemUser, companyService);
        QueryWrapper<Subcontractor> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", subcontractorService.pageCorrelation(companies, current, pageSize, search, sort));
    }

    /**
     * 通过社区分包人员编号获取
     *
     * @param id
     *            编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/subcontractor/{id}")
    @ApiOperation("通过社区分包人员编号获取")
    public R getSubcontractorById(@ApiParam(name = "需要编辑的社区分包人员编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", subcontractorService.getCorrelation(id));
    }

    /**
     * 社区分包人员添加处理
     *
     * @param subcontractor
     *            社区分包人员对象
     * @return 视图页面
     */
    @PostMapping("/subcontractor")
    @ApiOperation("社区分包人员添加处理")
    public R subcontractorCreateHandle(@ApiParam(name = "需要添加的社区分包人员对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Subcontractor subcontractor) {
        if (subcontractorService.save(subcontractor)) {
            return R.ok();
        }
        throw new JsonException("添加社区分包人员失败！");
    }

    /**
     * 社区分包人员修改处理
     *
     * @param id
     *            要修改的社区分包人员编号
     * @param subcontractor
     *            社区分包人员对象
     * @return 视图页面
     */
    @PutMapping("/subcontractor/{id}")
    @ApiOperation("社区分包人员修改处理")
    public R subcontractorModifyHandle(@ApiParam(name = "要修改的社区分包人员编号", required = true) @PathVariable Long id,
        @ApiParam(name = "需要修改的社区分包人员对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) Subcontractor subcontractor) {
        Subcontractor one =
            subcontractorService.getOne(new LambdaQueryWrapper<Subcontractor>().eq(Subcontractor::getId, id));
        subcontractor.setId(id).setVersion(one.getVersion());
        if (subcontractorService.updateById(subcontractor)) {
            return R.ok();
        }
        throw new JsonException("社区分包人员修改失败！");
    }

    /**
     * 通过社区分包人员编号删除
     *
     * @param id
     *            需要删除的社区分包人员编号
     * @return 是否删除成功
     */
    @DeleteMapping("/subcontractor/{id}")
    @ApiOperation("通过社区分包人员编号删除")
    public R removeSubcontractor(@ApiParam(name = "需要删除的社区分包人员编号", required = true) @PathVariable Long id) {
        if (subcontractorService.removeById(id)) {
            return R.ok("删除社区分包人员成功！");
        }
        throw new JsonException("删除社区分包人员失败！");
    }

    /**
     * 社区分包人员表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 社区分包人员表单列表JSON
     */
    @GetMapping("/subcontractor/select-list")
    @ApiOperation("社区分包人员表单列表")
    public R subcontractorSelectList(Long[] parentIds) {
        return R.ok().put("data", subcontractorService.treeSelectList(parentIds));
    }

    /**
     * 社区分包人员增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/subcontractor/batch")
    @ApiOperation("社区分包人员增删改批量操作")
    public R subcontractorBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            if (subcontractorService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 社区分包人员图表
     *
     * @param computedVo
     *            计算视图对象
     * @return Ajax返回JSON对象
     */
    @PostMapping("/subcontractor/computed/chart")
    @ApiOperation("社区分包人员图表")
    public R subcontractorChart(@ApiParam(name = "计算视图对象") @RequestBody(required = false) ComputedVo computedVo) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Configuration> configurationMap = configurationService.mapAll();
        return R.ok().put("data",
            subcontractorService.getBarChart(currentSystemUser.getCompanies(), getCompanyIds(computedVo),
                companyService.list(), null,
                Convert.toLong(configurationMap.get("system_administrator_id").getContent())));
    }
}
