package com.github.phonenumbermanager.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
    public R subdistrictList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据") Integer limit) {
        return R.ok().put("subdistrict", subdistrictService.getCorrelation(page, limit));
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
    public R getSubdistrict(@ApiParam(name = "查找的对应编号", required = true) @PathVariable Long id) {
        return R.ok().put("subdistrict", subdistrictService.getCorrelation(id));
    }

    /**
     * 添加街道处理
     *
     * @param subdistrict
     *            街道对象
     * @return 视图页面
     */
    @PostMapping
    @ApiOperation("添加街道处理")
    public R subdistrictCreateHandle(@ApiParam(name = "街道对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Subdistrict subdistrict) {
        if (subdistrictService.save(subdistrict)) {
            return setPhoneNumbers(phoneNumberService, subdistrict.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.SUBDISTRICT, String.valueOf(subdistrict.getId()));
        }
        throw new JsonException("添加街道失败！");
    }

    /**
     * 修改街道处理
     *
     * @param subdistrict
     *            街道对象
     * @return 视图页面
     */
    @PutMapping
    @ApiOperation("修改街道处理")
    public R subdistrictModifyHandle(@ApiParam(name = "街道对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) Subdistrict subdistrict) {
        if (subdistrictService.updateById(subdistrict)) {
            return setPhoneNumbers(phoneNumberService, subdistrict.getPhoneNumbers(),
                PhoneNumberSourceTypeEnum.SUBDISTRICT, String.valueOf(subdistrict.getId()));
        }
        throw new JsonException("修改街道失败！");
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
    public R deleteSubdistrict(@ApiParam(name = "通过街道编号删除", required = true) @PathVariable Long id) {
        if (subdistrictService.removeCorrelationById(id)) {
            return R.ok();
        }
        throw new JsonException("删除街道失败！");
    }
}
