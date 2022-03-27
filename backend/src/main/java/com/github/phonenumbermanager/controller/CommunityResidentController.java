package com.github.phonenumbermanager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.CommunityResidentSearchVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 社区居民控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/resident")
@Api(tags = "社区居民控制器")
public class CommunityResidentController extends BaseController {
    private final CommunityResidentService communityResidentService;

    /**
     * 社区居民列表
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据数量
     * @param communityResidentSearchVo
     *            社区居民搜索视图对象
     * @return 社区居民对象集合
     */
    @GetMapping
    @ApiOperation("社区居民列表")
    public R communityResidentList(@ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据数量") Integer pageSize,
        @ApiParam(name = "社区居民搜索视图对象") CommunityResidentSearchVo communityResidentSearchVo) {
        getEnvironmentVariable();
        return R.ok().put("data", communityResidentService.page(systemUser.getCompanies(), communityResidentSearchVo,
            new Page<>(current, pageSize)));
    }

    /**
     * 通过编号查找社区居民
     *
     * @param id
     *            需要查找的社区居民的编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ApiOperation("通过编号查找社区居民")
    public R getCommunityResidentById(@ApiParam(name = "需要查找的社区居民的编号", required = true) @PathVariable Long id) {
        return R.ok().put("data", communityResidentService.getCorrelation(id));
    }

    /**
     * 添加社区居民处理
     *
     * @param communityResident
     *            前台传递的社区居民对象
     * @return 视图页面
     */
    @PostMapping
    @ApiOperation("添加社区居民处理")
    public R communityResidentCreateHandle(@ApiParam(name = "前台传递的社区居民对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) CommunityResident communityResident) {
        CommunityResident resident = multiplePhoneHandler(communityResident);
        if (communityResidentService.save(resident)) {
            return R.ok();
        }
        throw new JsonException("添加社区居民失败！");
    }

    /**
     * 修改社区居民处理
     *
     * @param communityResident
     *            前台传递的社区居民对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("修改社区居民处理")
    public R communityResidentModifyHandle(@ApiParam(name = "前台传递的社区居民对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) CommunityResident communityResident) {
        if (communityResidentService.updateById(communityResident)) {
            return R.ok();
        }
        throw new JsonException("修改社区居民失败！");
    }

    /**
     * 通过社区居民编号删除社区居民
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ApiOperation("通过社区居民编号删除社区居民")
    public R removeCommunityResident(@ApiParam(name = "社区居民编号", required = true) @PathVariable Long id) {
        if (communityResidentService.removeById(id)) {
            return R.ok();
        }
        throw new JsonException("删除社区居民失败！");
    }

    /**
     * 导入居民信息进系统
     *
     * @param request
     *            HTTP请求对象
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ApiOperation("导入居民信息进系统")
    public R communityResidentImportAsSystem(HttpServletRequest request) {
        getEnvironmentVariable();
        int startRowNumber =
            Convert.toInt(configurationMap.get("read_dormitory_excel_start_row_number").get("content"));
        List<List<Object>> data = uploadExcelFileToData(request, startRowNumber);
        if (data != null && !communityResidentService.save(data, configurationMap)) {
            return R.ok();
        }
        throw new JsonException("上传文件失败！");
    }

    /**
     * 导出居民信息到Excel
     *
     * @param response
     *            前台响应对象
     * @param companyId
     *            单位编号
     */
    /*@GetMapping("/download")
    @ApiOperation("导出居民信息到Excel")
    public void communityResidentSaveAsExcel(HttpServletResponse response,
        @ApiParam(name = "需要生成的Excel表单位编号", required = true) Long companyId) {
        String excelResidentTitleUp = Convert.toStr(configurationMap.get("excel_resident_title_up").get("content"));
        String excelResidentTitle = Convert.toStr(configurationMap.get("excel_resident_title").get("content"));
        List<LinkedHashMap<String, Object>> dataResult =
            communityResidentService.listCorrelationToMap(systemUser.getCompanies());
        if (!dataResult.isEmpty()) {
            ExcelWriter excelWriter = ExcelUtil.getWriter();
            CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, true, false, false);
            excelWriter.writeCellValue(0, 0, excelResidentTitleUp);
            excelWriter.passCurrentRow();
            CellStyle titleStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(titleStyle, excelWriter, "方正小标宋简体", (short)16, false, false, false);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0,
                dataResult.get(0).keySet().size(), excelResidentTitle, titleStyle);
            excelWriter.passCurrentRow();
            CellStyle dateRowStyle = excelWriter.getOrCreateCellStyle(6, excelWriter.getCurrentRow());
            setCellStyle(dateRowStyle, excelWriter, "宋体", (short)11, false, false, true);
            excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 6, 1, new Date(), dateRowStyle);
            excelWriter.passCurrentRow();
            StyleSet styleSet = excelWriter.getStyleSet();
            CellStyle headCellStyle = styleSet.getHeadCellStyle();
            setCellStyle(headCellStyle, excelWriter, "宋体", (short)11, false, true, false);
            CellStyle cellStyle = styleSet.getCellStyle();
            setCellStyle(cellStyle, excelWriter, "宋体", (short)9, false, true, true);
            excelWriter.write(dataResult, true);
            excelWriter.setColumnWidth(-1, 22);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition",
                "attachment;filename=" + excelResidentTitle + System.currentTimeMillis() + ".xlsx");
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                excelWriter.flush(outputStream, true);
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException("导出Excel文件失败！");
            } finally {
                excelWriter.close();
            }
        }
    }*/

    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident
     *            需要处理的社区居民对象
     * @return 处理后的社区居民对象
     */
    private CommunityResident multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setName(Convert.toDBC(StrUtil.cleanBlank(communityResident.getName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident
            .setAddress(Convert.toDBC(StrUtil.cleanBlank(communityResident.getAddress())).replaceAll("—", "-"));
        return communityResident;
    }
}
