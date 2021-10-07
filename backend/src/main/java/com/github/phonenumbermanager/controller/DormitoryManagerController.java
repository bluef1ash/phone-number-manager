package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.enums.GenderEnum;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
    @Resource
    private CommunityService communityService;
    @Resource
    private PhoneNumberService phoneNumberService;

    /**
     * 社区楼长列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据
     * @param isSearch
     *            是否搜索
     * @param companyId
     *            单位编号
     * @param companyType
     *            单位类别编号
     * @param name
     *            社区楼长姓名
     * @param gender
     *            社区楼长性别
     * @param address
     *            社区楼长家庭地址
     * @param phone
     *            社区楼长联系方式
     * @return JSON对象
     */
    @GetMapping
    @ApiOperation("社区楼长列表")
    public R dormitoryManagerList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit,
        @ApiParam(name = "是否搜索") Boolean isSearch, @ApiParam(name = "单位编号") Long companyId,
        @ApiParam(name = "单位类别编号") Integer companyType, @ApiParam(name = "社区楼长姓名") String name,
        @ApiParam(name = "社区楼长性别") GenderEnum gender, @ApiParam(name = "社区楼长家庭地址") String address,
        @ApiParam(name = "社区楼长联系方式") String phone) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(6);
        jsonMap.put("systemCompanyType", systemCompanyType);
        jsonMap.put("communityCompanyType", communityCompanyType);
        jsonMap.put("subdistrictCompanyType", subdistrictCompanyType);
        // TODO: 2021/9/12 0012 用户权限
        // jsonMap.put("companyType", systemUser.getLevel());
        IPage<DormitoryManager> dormitoryManagers;
        if (isSearch != null && isSearch) {
            DormitoryManager dormitoryManager = new DormitoryManager();
            dormitoryManager.setName(name);
            dormitoryManager.setGender(gender);
            dormitoryManager.setAddress(address);
            List<String> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(phone);
            dormitoryManager.setPhoneNumbers(CommonUtil.setPhoneNumbers(phoneNumbers));
            dormitoryManagers = dormitoryManagerService.get(systemUser, systemCompanyType, communityCompanyType,
                subdistrictCompanyType, dormitoryManager, companyId, companyType, page, limit);
        } else {
            dormitoryManagers = dormitoryManagerService.getCorrelation(systemUser,
                PhoneNumberSourceTypeEnum.DORMITORY_MANAGER, communityCompanyType, subdistrictCompanyType, page, limit);
        }
        jsonMap.put("dormitoryManagers", dormitoryManagers);
        return R.ok(jsonMap);
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
    public R findDormitoryManager(@ApiParam(name = "社区楼长的编号", required = true) @PathVariable String id) {
        return R.ok().put("dormitoryManager", dormitoryManagerService.getCorrelation(id));
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
        if (communityService.isSubmittedById(1, dormitoryManager.getCommunityId())) {
            throw new JsonException("此社区已经提报，不允许添加！");
        }
        if (dormitoryManagerService.save(dormitoryManager)) {
            return setPhoneNumbers(phoneNumberService, dormitoryManager.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.DORMITORY_MANAGER, dormitoryManager.getId());
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
            return setPhoneNumbers(phoneNumberService, dormitoryManager.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.COMMUNITY, dormitoryManager.getId());
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
    public R deleteDormitoryManager(@ApiParam(name = "社区楼长编号", required = true) @PathVariable String id) {
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
     * @param subdistrictId
     *            导入的街道编号
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ApiOperation("导入楼长信息进系统")
    public R dormitoryManagerImportAsSystem(HttpServletRequest request,
        @ApiParam(name = "导入的街道编号", required = true) Long subdistrictId) {
        List<List<Object>> data =
            uploadExcel(request, Convert.toInt(configurationsMap.get("read_dormitory_excel_start_row_number")));
        if (data != null && dormitoryManagerService.save(data, subdistrictId, configurationsMap)) {
            return R.ok("上传成功！");
        }
        throw new JsonException("上传文件失败！");
    }

    /**
     * 导出社区楼长信息到Excel
     *
     * @param response
     *            前台响应对象
     * @param data
     *            传递数据
     */
    @GetMapping("/download")
    public void dormitoryManagerSaveAsExcel(HttpServletResponse response, @RequestParam String data) {
        List<Map<String, Object>> userData = getDecodeData(data);
        String excelDormitoryTitleUp = Convert.toStr(configurationsMap.get("excel_dormitory_title_up"));
        String excelDormitoryTitle = Convert.toStr(configurationsMap.get("excel_dormitory_title"));
        // 获取业务数据集
        List<LinkedHashMap<String, Object>> dataResult =
            dormitoryManagerService.getCorrelation(communityCompanyType, subdistrictCompanyType, userData);
        if (!dataResult.isEmpty()) {
            ExcelWriter excelWriter = ExcelUtil.getWriter();
            CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, false, false, false);
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

    /**
     * 通过社区编号加载最后一个编号
     *
     * @param communityId
     *            社区编号
     * @param communityName
     *            社区名称
     * @param subdistrictName
     *            街道办事处名称
     * @return JSON数据
     */
    @PostMapping("/last_id")
    @ApiOperation("通过社区编号加载最后一个编号")
    public R loadDormitoryManagerLastId(@ApiParam(name = "社区编号") Long communityId,
        @ApiParam(name = "社区名称") String communityName, @ApiParam(name = "街道办事处名称") String subdistrictName) {
        String lastId = dormitoryManagerService.get(communityId, communityName, subdistrictName);
        return R.ok().put("id", lastId);
    }
}
