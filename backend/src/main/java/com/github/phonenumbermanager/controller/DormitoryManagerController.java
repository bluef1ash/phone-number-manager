package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.DormitoryManagerSearchVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 社区楼长控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/dormitory")
@Api(tags = "社区楼长控制器")
public class DormitoryManagerController extends BaseController {
    @Resource
    private DormitoryManagerService dormitoryManagerService;

    /**
     * 社区楼长列表
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return JSON对象
     */
    @GetMapping
    @ApiOperation("社区楼长列表")
    public R dormitoryManagerList(@ApiParam(name = "分页页码") Integer current, @ApiParam(name = "每页数据") Integer pageSize,
        @ApiParam(name = "社区楼片长搜索对象") DormitoryManagerSearchVo dormitoryManagerSearchVo) {
        getEnvironmentVariable();
        Page<DormitoryManager> dormitoryManagerPage = new Page<>(current, pageSize);
        return R.ok().put("data",
            dormitoryManagerService.page(systemUser.getCompanies(), dormitoryManagerSearchVo, dormitoryManagerPage));
    }

    /**
     * 通过编号查找社区楼长
     *
     * @param id
     *            社区楼长的编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ApiOperation("通过编号查找社区楼长")
    public R getDormitoryManagerById(@ApiParam(name = "社区楼长的编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", dormitoryManagerService.getCorrelation(id));
    }

    /**
     * 添加社区楼长处理
     *
     * @param dormitoryManager
     *            前台传递的社区楼长对象
     * @return 视图页面
     */
    @PostMapping
    @ApiOperation("添加社区楼长处理")
    public R dormitoryManagerCreateHandle(@ApiParam(name = "前台传递的社区楼长对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) DormitoryManager dormitoryManager) {
        if (dormitoryManagerService.save(dormitoryManager)) {
            return R.ok();
        }
        throw new JsonException("添加社区楼长失败！");
    }

    /**
     * 修改社区楼长处理
     *
     * @param dormitoryManager
     *            前台传递的社区楼长对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("修改社区楼长处理")
    public R dormitoryManagerModifyHandle(@ApiParam(name = "前台传递的社区楼长对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) DormitoryManager dormitoryManager) {
        if (dormitoryManagerService.updateById(dormitoryManager)) {
            return R.ok();
        }
        throw new JsonException("修改社区楼长失败！");
    }

    /**
     * 通过社区楼长编号删除
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ApiOperation("通过社区楼长编号删除")
    public R removeDormitoryManager(@ApiParam(name = "社区楼长编号", required = true) @PathVariable Long id) {
        if (dormitoryManagerService.removeCorrelationById(id)) {
            return R.ok("删除社区楼长成功！");
        }
        throw new JsonException("删除社区楼长失败！");
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
     * 导出社区楼长信息到Excel
     *
     * @param response
     *            前台响应对象
     * @param companyId
     *            单位编号
     */
    @GetMapping("/download")
    @ApiOperation("导出社区楼长信息到Excel")
    public void dormitoryManagerSaveAsExcel(HttpServletResponse response,
        @ApiParam(name = "需要生成的Excel表单位编号", required = true) Long companyId) {
        getEnvironmentVariable();
        String excelDormitoryTitleUp = Convert.toStr(configurationMap.get("excel_dormitory_title_up").get("content"));
        String excelDormitoryTitle = Convert.toStr(configurationMap.get("excel_dormitory_title").get("content"));
        // 获取业务数据集
        List<LinkedHashMap<String, Object>> dataResult = dormitoryManagerService.listCorrelationToMap(companyId);
        if (!dataResult.isEmpty()) {
            ExcelWriter excelWriter = ExcelUtil.getWriter();
            CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, 1, excelDormitoryTitleUp,
                firstRowStyle);
            excelWriter.passCurrentRow();
            CellStyle titleStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(titleStyle, excelWriter, "方正小标宋简体", (short)16, false, false, false);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0,
                dataResult.get(0).keySet().size(), excelDormitoryTitle, titleStyle);
            excelWriter.passCurrentRow();
            StyleSet styleSet = excelWriter.getStyleSet();
            CellStyle headCellStyle = styleSet.getHeadCellStyle();
            setCellStyle(headCellStyle, excelWriter, "宋体", (short)11, false, true, false);
            CellStyle cellStyle = styleSet.getCellStyle();
            setCellStyle(cellStyle, excelWriter, "宋体", (short)9, false, true, true);
            Map<String, String> tableHead = new LinkedHashMap<>();
            tableHead.put("sequenceNumber", "序号");
            tableHead.put("communityName", "社区名称");
            tableHead.put("id", "编号");
            tableHead.put("name", "姓名");
            tableHead.put("genderName", "性别");
            tableHead.put("birthString", "出生年月");
            tableHead.put("politicalStatusName", "政治面貌");
            tableHead.put("workStatusName", "工作状况");
            tableHead.put("educationName", "文化程度");
            tableHead.put("address", "家庭住址（具体到单元号、楼号）");
            tableHead.put("managerAddress", "分包楼栋（具体到单元号、楼号）");
            tableHead.put("managerCount", "联系户数");
            tableHead.put("telephone", "手机");
            tableHead.put("landline", "座机");
            tableHead.put("subcontractorName", "姓名");
            tableHead.put("subcontractorTelephone", "手机");
            excelWriter.setHeaderAlias(tableHead);
            int index = 0;
            for (String value : tableHead.values()) {
                if (index == tableHead.size() - 2) {
                    excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), index, index + 1, "分包人",
                        true);
                    excelWriter.writeCellValue(excelWriter.getCurrentRow() + 1, index, value);
                } else if (index == tableHead.size() - 1) {
                    excelWriter.writeCellValue(excelWriter.getCurrentRow() + 1, index, value);
                } else {
                    excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow() + 1, index, index, value,
                        true);
                }
                index++;
            }
            excelWriter.passCurrentRow();
            excelWriter.passCurrentRow();
            excelWriter.write(dataResult, false);
            excelWriter.setColumnWidth(-1, 22);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition",
                "attachment;filename=" + excelDormitoryTitle + System.currentTimeMillis() + ".xlsx");
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
}
