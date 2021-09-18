package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.validator.CommunityResidentInputValidator;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 社区居民控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/resident")
@Api(tags = "社区居民控制器")
public class CommunityResidentController extends BaseController {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private CommunityService communityService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod())
            || RequestMethod.PUT.toString().equals(request.getMethod())) {
            binder.replaceValidators(new CommunityResidentInputValidator(communityResidentService, request));
        }
    }

    /**
     * 社区居民列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据数量
     * @param companyId
     *            单位编号
     * @param companyType
     *            单位类别编号
     * @param name
     *            社区居民姓名
     * @param address
     *            社区居民家庭地址
     * @param phone
     *            社区居民联系方式
     * @param isSearch
     *            是否搜索
     * @return JSON对象
     */
    @GetMapping
    @ApiOperation("社区居民列表")
    public Map<String, Object> communityResidentList(@ApiParam(name = "分页页码") Integer page,
        @ApiParam(name = "每页数据数量") Integer limit, @ApiParam(name = "单位编号") Long companyId,
        @ApiParam(name = "单位类别编号") Long companyType, @ApiParam(name = "社区居民姓名") String name,
        @ApiParam(name = "社区居民家庭地址") String address, @ApiParam(name = "社区居民联系方式") String phone,
        @ApiParam(name = "是否为搜索状态") Boolean isSearch) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(6);
        jsonMap.put("systemCompanyType", systemCompanyType);
        jsonMap.put("communityCompanyType", communityCompanyType);
        jsonMap.put("subdistrictCompanyType", subdistrictCompanyType);
        // TODO: 2021/9/12 0012 用户权限
        // jsonMap.put("companyType", systemUser.getLevel());
        jsonMap.put("dataType", 0);
        IPage<CommunityResident> communityResidents;
        if (isSearch != null && isSearch) {
            CommunityResident communityResident = new CommunityResident();
            communityResident.setName(name);
            communityResident.setAddress(address);
            List<String> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(phone);
            communityResident.setPhoneNumbers(CommonUtil.setPhoneNumbers(phoneNumbers));
            communityResidents = communityResidentService.get(systemUser, systemAdministratorId, communityCompanyType,
                subdistrictCompanyType, communityResident, companyId, companyType, page, limit);
        } else {
            communityResidents =
                communityResidentService.getCorrelation(systemUser, PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT,
                    communityCompanyType, subdistrictCompanyType, page, limit);
        }
        jsonMap.put("communityResidents", communityResidents);
        return jsonMap;
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
    public CommunityResident
        editCommunityResident(@ApiParam(name = "需要查找的社区居民的编号", required = true) @PathVariable Long id) {
        return communityResidentService.getCorrelation(id);
    }

    /**
     * 添加、修改社区居民处理
     *
     * @param httpServletRequest
     *            HTTP请求对象
     * @param communityResident
     *            前台传递的社区居民对象
     * @param bindingResult
     *            错误信息对象
     * @return 视图页面
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加、修改社区居民处理")
    public Map<String, Object> communityResidentCreateOrEditHandle(HttpServletRequest httpServletRequest,
        @ApiParam(name = "前台传递的社区居民对象", required = true) @RequestBody @Validated CommunityResident communityResident,
        BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
        }
        if (RequestMethod.POST.toString().equals(httpServletRequest.getMethod())) {
            if (communityService.isSubmittedById(0, communityResident.getCommunityId())) {
                throw new JsonException("此社区已经提报，不允许添加！");
            }
            multiplePhoneHandler(communityResident);
            if (communityResidentService.save(communityResident)) {
                setPhoneNumbers(communityResident.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT,
                    String.valueOf(communityResident.getId()));
                phoneNumberService.saveBatch(communityResident.getPhoneNumbers());
            } else {
                throw new JsonException("添加社区居民失败！");
            }
        } else {
            if (communityResidentService.updateById(communityResident)) {
                setPhoneNumbers(communityResident.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT,
                    String.valueOf(communityResident.getId()));
                phoneNumberService.saveOrUpdateBatch(communityResident.getPhoneNumbers());
            } else {
                throw new JsonException("修改社区居民失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
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
    public Map<String, Object>
        deleteCommunityResident(@ApiParam(name = "社区居民编号", required = true) @PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (communityResidentService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区居民成功！");
            return jsonMap;
        }
        throw new JsonException("删除社区居民失败！");
    }

    /**
     * 导入居民信息进系统
     *
     * @param request
     *            HTTP请求对象
     * @param subdistrictId
     *            导入的街道编号
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ApiOperation("导入居民信息进系统")
    public Map<String, Object> communityResidentImportAsSystem(HttpServletRequest request,
        @ApiParam(name = "导入的街道编号", required = true) Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        int startRowNumber = Convert.toInt(configurationsMap.get("read_dormitory_excel_start_row_number"));
        try {
            List<List<Object>> data = uploadExcel(request, startRowNumber);
            jsonMap.put("state", 1);
            jsonMap.put("create", communityResidentService.save(data, subdistrictId, configurationsMap));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("上传文件失败！", e);
        }
    }

    /**
     * 导出居民信息到Excel
     *
     * @param response
     *            前台响应对象
     * @param data
     *            传递数据
     */
    @GetMapping("/download")
    public void communityResidentSaveAsExcel(HttpServletResponse response, String data) {
        List<Map<String, Object>> userData = getDecodeData(data);
        List<LinkedHashMap<String, Object>> dataResult =
            communityResidentService.getCorrelation(communityCompanyType, subdistrictCompanyType, userData);
        String excelResidentTitleUp = Convert.toStr(configurationsMap.get("excel_resident_title_up"));
        String excelResidentTitle = Convert.toStr(configurationsMap.get("excel_resident_title"));
        if (!dataResult.isEmpty()) {
            ExcelWriter excelWriter = ExcelUtil.getWriter();
            CellStyle firstRowStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
            setCellStyle(firstRowStyle, excelWriter, "宋体", (short)12, false, false, false);
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
     * 添加、修改到数据库前处理
     *
     * @param communityResident
     *            需要处理的社区居民对象
     */
    private void multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setName(Convert.toDBC(StrUtil.cleanBlank(communityResident.getName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident
            .setAddress(Convert.toDBC(StrUtil.cleanBlank(communityResident.getAddress())).replaceAll("—", "-"));
    }
}
