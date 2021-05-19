package com.github.phonenumbermanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;
import com.github.phonenumbermanager.vo.ComputedVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 社区居民楼片长控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Controller
@RequestMapping("/dormitory")
@Api(tags = "社区居民楼片长控制器")
public class DormitoryManagerController extends BaseController {
    private final DormitoryManagerService dormitoryManagerService;
    private final CompanyService companyService;

    /**
     * 社区居民楼片长列表
     *
     * @param request
     *            HTTP请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return JSON对象
     */
    @GetMapping
    @ResponseBody
    @ApiOperation("社区居民楼片长列表")
    public R dormitoryManagerList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据") Integer pageSize) {
        getEnvironmentVariable();
        getSearchParameter(request);
        return R.ok().put("data",
            dormitoryManagerService.pageCorrelation(currentSystemUser.getCompanies(), current, pageSize, search, sort));
    }

    /**
     * 通过编号查找社区居民楼片长
     *
     * @param id
     *            社区居民楼片长的编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation("通过编号查找社区居民楼片长")
    public R getDormitoryManagerById(@ApiParam(name = "社区居民楼片长的编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", dormitoryManagerService.getCorrelation(id));
    }

    /**
     * 添加社区居民楼片长处理
     *
     * @param dormitoryManager
     *            前台传递的社区居民楼片长对象
     * @return 视图页面
     */
    @PostMapping
    @ResponseBody
    @ApiOperation("添加社区居民楼片长处理")
    public R dormitoryManagerCreateHandle(@ApiParam(name = "前台传递的社区居民楼片长对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) DormitoryManager dormitoryManager) {
        if (dormitoryManagerService.save(dormitoryManager)) {
            return R.ok();
        }
        throw new JsonException("添加社区居民楼片长失败！");
    }

    /**
     * 修改社区居民楼片长处理
     *
     * @param id
     *            要修改的单位编号
     * @param dormitoryManager
     *            前台传递的社区居民楼片长对象
     * @return 视图页面
     */
    @PutMapping("/{id}")
    @ResponseBody
    @ApiOperation("修改社区居民楼片长处理")
    public R dormitoryManagerModifyHandle(@ApiParam(name = "要修改的社区居民楼片长编号", required = true) @PathVariable Long id,
        @ApiParam(name = "前台传递的社区居民楼片长对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) DormitoryManager dormitoryManager) {
        dormitoryManager.setId(id).setVersion(dormitoryManagerService.getOne(new LambdaQueryWrapper<DormitoryManager>()
            .eq(DormitoryManager::getId, id).select(DormitoryManager::getVersion)).getVersion());
        if (dormitoryManagerService.updateById(dormitoryManager)) {
            return R.ok();
        }
        throw new JsonException("修改社区居民楼片长失败！");
    }

    /**
     * 通过社区居民楼片长编号删除
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    @ApiOperation("通过社区居民楼片长编号删除")
    public R removeDormitoryManager(@ApiParam(name = "社区居民楼片长编号", required = true) @PathVariable Long id) {
        if (dormitoryManagerService.removeById(id)) {
            return R.ok("删除社区居民楼片长成功！");
        }
        throw new JsonException("删除社区居民楼片长失败！");
    }

    /**
     * 导入社区居民楼片长信息进系统
     *
     * @param request
     *            HTTP请求对象
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ResponseBody
    @ApiOperation("导入社区居民楼片长信息进系统")
    public R dormitoryManagerImportAsSystem(HttpServletRequest request) {
        getEnvironmentVariable();
        List<List<Object>> data = uploadExcelFileToData(request,
            Convert.toInt(configurationMap.get("read_dormitory_excel_start_row_number").get("content")));
        if (data != null && dormitoryManagerService.save(data, configurationMap)) {
            return R.ok("上传成功！");
        }
        throw new JsonException("上传文件失败！");
    }

    /**
     * 导出社区居民楼片长信息到Excel
     *
     * @param response
     *            前台响应对象
     */
    @GetMapping("/download")
    @ApiOperation("导出社区居民楼片长信息到Excel")
    public void dormitoryManagerSaveAsExcel(HttpServletResponse response) {
        getEnvironmentVariable();
        ExcelWriter excelWriter =
            dormitoryManagerService.listCorrelationExportExcel(currentSystemUser, configurationMap);
        downloadExcelFile(response, excelWriter, "社区楼片长花名册");
    }

    /**
     * 社区居民楼片长增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/batch")
    @ResponseBody
    @ApiOperation("社区居民楼片长增删改批量操作")
    public R dormitoryManagerBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            if (dormitoryManagerService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 社区居民楼片长基础数据
     *
     * @param computedVo
     *            计算视图对象
     * @return Ajax返回JSON对象
     */
    @PostMapping("/computed/message")
    @ResponseBody
    @ApiOperation("社区居民楼片长基础数据")
    public R
        dormitoryManagerBaseMessage(@ApiParam(name = "计算视图对象") @RequestBody(required = false) ComputedVo computedVo) {
        getEnvironmentVariable();
        return R.ok().put("data",
            dormitoryManagerService.getBaseMessage(currentSystemUser.getCompanies(), getCompanyIds(computedVo)));
    }

    /**
     * 社区居民楼片长图表
     *
     * @param computedVo
     *            计算视图对象
     * @return Ajax返回JSON对象
     */
    @PostMapping("/computed/chart")
    @ResponseBody
    @ApiOperation("社区居民楼片长图表")
    public R dormitoryManagerChart(@ApiParam(name = "计算视图对象") @RequestBody(required = false) ComputedVo computedVo) {
        getEnvironmentVariable();
        return R.ok().put("data", dormitoryManagerService.getBarChart(currentSystemUser.getCompanies(),
            getCompanyIds(computedVo), companyService.list(), "辖区居民楼片长数"));
    }
}
