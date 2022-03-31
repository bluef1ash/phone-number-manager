package com.github.phonenumbermanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import cn.hutool.core.convert.Convert;
import cn.hutool.poi.excel.ExcelWriter;
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
@Controller
@RequestMapping("/resident")
@Api(tags = "社区居民控制器")
public class CommunityResidentController extends BaseController {
    private final CommunityResidentService communityResidentService;

    /**
     * 社区居民列表
     *
     * @param request
     *            HTTP请求对象
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据数量
     * @return 社区居民对象集合
     */
    @GetMapping
    @ResponseBody
    @ApiOperation("社区居民列表")
    public R communityResidentList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据数量") Integer pageSize) {
        getEnvironmentVariable();
        getSearchParameter(request);
        return R.ok().put("data",
            communityResidentService.pageCorrelation(systemUser.getCompanies(), current, pageSize, search, sort));
    }

    /**
     * 通过编号查找社区居民
     *
     * @param id
     *            需要查找的社区居民的编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ResponseBody
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
    @ResponseBody
    @ApiOperation("添加社区居民处理")
    public R communityResidentCreateHandle(@ApiParam(name = "前台传递的社区居民对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) CommunityResident communityResident) {
        String repeatMessage = validatePhoneNumber(communityResident.getPhoneNumbers());
        if (repeatMessage != null) {
            return R.error(ExceptionCode.REPEAT_DATA_FAILED.getCode(), repeatMessage);
        }
        if (communityResidentService.save(communityResident)) {
            return R.ok();
        }
        throw new JsonException("添加社区居民失败！");
    }

    /**
     * 修改社区居民处理
     *
     * @param id
     *            要修改的社区居民编号
     * @param communityResident
     *            前台传递的社区居民对象
     * @return 视图页面
     */
    @PutMapping("/{id}")
    @ResponseBody
    @ApiOperation("修改社区居民处理")
    public R communityResidentModifyHandle(@ApiParam(name = "要修改的社区居民编号", required = true) @PathVariable Long id,
        @ApiParam(name = "前台传递的社区居民对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) CommunityResident communityResident) {
        String repeatMessage = validatePhoneNumber(communityResident.getPhoneNumbers());
        if (repeatMessage != null) {
            return R.error(ExceptionCode.REPEAT_DATA_FAILED.getCode(), repeatMessage);
        }
        communityResident.setId(id)
            .setVersion(communityResidentService.getOne(new LambdaQueryWrapper<CommunityResident>()
                .eq(CommunityResident::getId, id).select(CommunityResident::getVersion)).getVersion());
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
    @ResponseBody
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
    @ResponseBody
    @ApiOperation("导入居民信息进系统")
    public R communityResidentImportAsSystem(HttpServletRequest request) {
        getEnvironmentVariable();
        int startRowNumber = Convert.toInt(configurationMap.get("read_resident_excel_start_row_number").get("content"));
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
     */
    @GetMapping("/download")
    @ApiOperation("导出居民信息到Excel")
    public void communityResidentSaveAsExcel(HttpServletResponse response) {
        getEnvironmentVariable();
        ExcelWriter excelWriter =
            communityResidentService.listCorrelationExportExcel(systemUser.getCompanies(), configurationMap);
        downloadExcelFile(response, excelWriter, excelWriter.getCell(0, 1).getStringCellValue());
    }

    /**
     * 验证联系方式
     *
     * @param phoneNumbers
     *            需要验证的数据集合
     */
    private String validatePhoneNumber(List<PhoneNumber> phoneNumbers) {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            List<CommunityResident> communityResidents = communityResidentService.listByPhoneNumbers(phoneNumbers);
            if (!communityResidents.isEmpty()) {
                StringBuilder output = new StringBuilder();
                for (CommunityResident communityResident : communityResidents) {
                    String phone = communityResident.getPhoneNumbers().stream()
                        .map(phoneNumber -> "输入的联系方式：" + phoneNumber.getPhoneNumber() + "，已经存在于"
                            + communityResident.getCompany().getName() + "单位的" + communityResident.getName() + "居民")
                        .collect(Collectors.joining(", "));
                    output.append(phone);
                }
                return output.toString();
            }
        }
        return null;
    }
}
