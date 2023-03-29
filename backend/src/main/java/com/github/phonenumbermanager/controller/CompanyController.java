package com.github.phonenumbermanager.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVO;
import com.github.phonenumbermanager.vo.ComputedVO;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

/**
 * 单位控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/company")
@Tag(name = "单位控制器")
public class CompanyController extends BaseController {
    private final CompanyService companyService;
    private final ConfigurationService configurationService;
    private final SubcontractorService subcontractorService;

    /**
     * 获取单位分页列表
     *
     * @param request
     *            HTTP 请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据条数
     * @return 单位分页对象集合
     */
    @GetMapping
    @Operation(summary = "获取单位分页列表")
    public R companyList(HttpServletRequest request, @Parameter(name = "分页页码") Integer current,
        @Parameter(name = "每页数据条数") Integer pageSize) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = getUserCompanies(configurationService.mapAll(), currentSystemUser, companyService);
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", companyService.pageCorrelation(companies, current, pageSize, search, sort));
    }

    /**
     * 通过单位编号获取单位的详细信息
     *
     * @param id
     *            查询的单位编号
     * @return 对应编号的单位的详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "通过单位编号获取单位的详细信息")
    public R getCompanyById(@Parameter(name = "需要编辑的单位编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", companyService.getCorrelation(id));
    }

    /**
     * 单位添加处理
     *
     * @param company
     *            单位对象
     * @return 添加成功或者失败
     */
    @PostMapping
    @Operation(summary = "单位添加处理")
    public R companyCreateHandle(@Parameter(name = "需要添加的单位对象",
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
     *            要修改的单位对象
     * @return 修改成功或者失败
     */
    @PutMapping("/{id}")
    @Operation(summary = "单位修改处理")
    public R companyModifyHandle(@Parameter(name = "要修改的单位编号", required = true) @PathVariable Long id,
        @Parameter(name = "需要修改的单位对象",
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
     * @return 删除添加成功或者失败
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "通过单位编号删除")
    public R removeCompany(@Parameter(name = "需要删除的单位编号", required = true) @PathVariable Long id) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (companyService.removeById(id, currentSystemUser.getId())) {
            return R.ok("删除社区成功！");
        }
        throw new JsonException("删除社区失败！");
    }

    /**
     * 获取单位表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 单位表单列表集合
     */
    @GetMapping("/select-list")
    @Operation(summary = "获取单位表单列表")
    public R companySelectList(Long[] parentIds) {
        return R.ok().put("data", companyService.treeSelectList(parentIds));
    }

    /**
     * 单位增删改批量操作
     *
     * @param batchRestfulVO
     *            批量操作视图对象
     * @return 批量操作成功或者失败
     */
    @PostMapping("/batch")
    @Operation(summary = "单位增删改批量操作")
    public R companyBatch(
        @Parameter(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVO batchRestfulVO) {
        if (batchRestfulVO.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVO.getData(), Long.class);
            if (companyService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 获取社区分包人员列表
     *
     * @param request
     *            HTTP 请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据条数
     * @return 社区分包人员分页对象集合
     */
    @GetMapping("/subcontractor")
    @Operation(summary = "获取社区分包人员列表")
    public R subcontractorList(HttpServletRequest request, @Parameter(name = "分页页码") Integer current,
        @Parameter(name = "每页数据条数") Integer pageSize) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = getUserCompanies(configurationService.mapAll(), currentSystemUser, companyService);
        QueryWrapper<Subcontractor> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", subcontractorService.pageCorrelation(companies, current, pageSize, search, sort));
    }

    /**
     * 通过社区分包人员编号获取社区分包人员的详细信息
     *
     * @param id
     *            查询社区分包人的编号
     * @return 对应编号社区分包人员的详细信息
     */
    @GetMapping("/subcontractor/{id}")
    @Operation(summary = "通过社区分包人员编号获取社区分包人员的详细信息")
    public R getSubcontractorById(@Parameter(name = "需要编辑的社区分包人员编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", subcontractorService.getCorrelation(id));
    }

    /**
     * 社区分包人员添加处理
     *
     * @param subcontractor
     *            社区分包人员对象
     * @return 添加成功或者失败
     */
    @PostMapping("/subcontractor")
    @Operation(summary = "社区分包人员添加处理")
    public R subcontractorCreateHandle(@Parameter(name = "需要添加的社区分包人员对象",
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
     *            需要修改的社区分包人员对象
     * @return 修改成功或者失败
     */
    @PutMapping("/subcontractor/{id}")
    @Operation(summary = "社区分包人员修改处理")
    public R subcontractorModifyHandle(@Parameter(name = "要修改的社区分包人员编号", required = true) @PathVariable Long id,
        @Parameter(name = "需要修改的社区分包人员对象",
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
     * @return 删除成功或者失败
     */
    @DeleteMapping("/subcontractor/{id}")
    @Operation(summary = "通过社区分包人员编号删除")
    public R removeSubcontractor(@Parameter(name = "需要删除的社区分包人员编号", required = true) @PathVariable Long id) {
        if (subcontractorService.removeById(id)) {
            return R.ok("删除社区分包人员成功！");
        }
        throw new JsonException("删除社区分包人员失败！");
    }

    /**
     * 获取社区分包人员表单列表
     *
     * @param parentIds
     *            父级编号数组
     * @return 社区分包人员表单列表集合
     */
    @GetMapping("/subcontractor/select-list")
    @Operation(summary = "获取社区分包人员表单列表")
    public R subcontractorSelectList(Long[] parentIds) {
        return R.ok().put("data", subcontractorService.treeSelectList(parentIds));
    }

    /**
     * 社区分包人员增删改批量操作
     *
     * @param batchRestfulVO
     *            批量操作视图对象
     * @return 批量操作成功或者失败
     */
    @PostMapping("/subcontractor/batch")
    @Operation(summary = "社区分包人员增删改批量操作")
    public R subcontractorBatch(
        @Parameter(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVO batchRestfulVO) {
        if (batchRestfulVO.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVO.getData(), Long.class);
            if (subcontractorService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 获取社区分包人员图表数据
     *
     * @param computedVo
     *            计算视图对象
     * @return 社区分包人员图表数据
     */
    @PostMapping("/subcontractor/computed/chart")
    @Operation(summary = "获取社区分包人员图表数据")
    public R subcontractorChart(@Parameter(name = "计算视图对象") @RequestBody(required = false) ComputedVO computedVo) {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok().put("data", subcontractorService.getBarChart(currentSystemUser.getCompanies(),
            getCompanyIds(computedVo), companyService.list(), null));
    }
}
