package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
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
            dormitoryManagerService.pageCorrelation(systemUser.getCompanies(), current, pageSize, search, sort));
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
     * @param dormitoryManager
     *            前台传递的社区居民楼片长对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("修改社区居民楼片长处理")
    public R dormitoryManagerModifyHandle(@ApiParam(name = "前台传递的社区居民楼片长对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) DormitoryManager dormitoryManager) {
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
     * 导入楼长信息进系统
     *
     * @param request
     *            HTTP请求对象
     * @param streetId
     *            导入的街道编号
     * @return Ajax信息
     */
    @PostMapping("/import/{streetId}")
    @ResponseBody
    @ApiOperation("导入楼长信息进系统")
    public R dormitoryManagerImportAsSystem(HttpServletRequest request,
        @ApiParam(name = "导入的街道编号", required = true) @PathVariable Long streetId) {
        getEnvironmentVariable();
        List<List<Object>> data = uploadExcel(request,
            Convert.toInt(configurationMap.get("read_dormitory_excel_start_row_number").get("content")));
        if (data != null && dormitoryManagerService.save(data, streetId, configurationMap)) {
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
            dormitoryManagerService.listCorrelationExportExcel(systemUser.getCompanies(), configurationMap);
        if (excelWriter != null) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLUtil
                .encode(excelWriter.getCell(0, 1).getStringCellValue() + System.currentTimeMillis() + ".xlsx"));
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                excelWriter.flush(outputStream, true);
                IoUtil.close(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException("导出Excel文件失败！");
            } finally {
                excelWriter.close();
            }
        }
    }

    /**
     * 增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/batch")
    @ResponseBody
    @ApiOperation("增删改批量操作")
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
}
