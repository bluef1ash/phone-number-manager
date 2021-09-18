package com.github.phonenumbermanager.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.validator.SubdistrictInputValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 街道控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/subdistrict")
@Api(tags = "街道控制器")
public class SubdistrictController extends BaseController {
    @Resource
    private SubdistrictService subdistrictService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod())
            || RequestMethod.PUT.toString().equals(request.getMethod())) {
            binder.replaceValidators(new SubdistrictInputValidator(subdistrictService, request));
        }
    }

    /**
     * 街道列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据量
     * @return 视图页面
     */
    @GetMapping
    @ApiOperation("街道列表")
    public IPage<Subdistrict> subdistrictList(@ApiParam(name = "分页页码") Integer page,
        @ApiParam(name = "每页数据") Integer limit) {
        return subdistrictService.getCorrelation(page, limit);
    }

    /**
     * 通过街道编号查找
     *
     * @param id
     *            查找的对应编号
     * @return 视图页面
     */
    @GetMapping("/{id}")
    @ApiOperation("通过街道编号查找")
    public Subdistrict getSubdistrict(@ApiParam(name = "查找的对应编号", required = true) @PathVariable Long id) {
        return subdistrictService.getCorrelation(id);
    }

    /**
     * 添加、修改街道处理
     *
     * @param request
     *            HTTP请求对象
     * @param subdistrict
     *            街道对象
     * @param bindingResult
     *            错误信息对象
     * @return 视图页面
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加、修改街道处理")
    public Map<String, Object> subdistrictCreateOrEditHandle(HttpServletRequest request,
        @ApiParam(name = "街道对象", required = true) @RequestBody @Validated Subdistrict subdistrict,
        BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (subdistrictService.save(subdistrict)) {
                setPhoneNumbers(subdistrict.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBDISTRICT,
                    String.valueOf(subdistrict.getId()));
                phoneNumberService.saveBatch(subdistrict.getPhoneNumbers());
            } else {
                throw new JsonException("添加街道失败！");
            }
        } else {
            if (subdistrictService.updateById(subdistrict)) {
                setPhoneNumbers(subdistrict.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBDISTRICT,
                    String.valueOf(subdistrict.getId()));
                phoneNumberService.saveOrUpdateBatch(subdistrict.getPhoneNumbers());
            } else {
                throw new JsonException("修改街道失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过街道编号删除
     *
     * @param id
     *            对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ApiOperation("通过街道编号删除")
    public Map<String, Object> deleteSubdistrict(@ApiParam(name = "通过街道编号删除", required = true) @PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        if (subdistrictService.removeById(id)
            && phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.SUBDISTRICT, id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除街道成功！");
            return jsonMap;
        }
        throw new JsonException("删除街道失败！");
    }
}
