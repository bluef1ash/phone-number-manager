package com.github.phonenumbermanager.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 系统管理控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/system")
@Api(tags = "系统管理控制器")
public class SystemController extends BaseController {
    @Resource
    private ConfigurationService configurationService;

    /**
     * 系统配置列表
     *
     * @param page
     *            分页页码
     * @param limit
     *            每页数据
     * @return 系统配置列表JSON
     */
    @GetMapping("/configuration")
    @ApiOperation("系统配置列表")
    public R configurationList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据数量") Integer limit) {
        return R.ok().put("configurations", configurationService.page(new Page<>(page, limit), null));
    }

    /**
     * 通过系统配置项编号查找
     *
     * @param id
     *            系统配置项编号
     * @return 系统配置项
     */
    @GetMapping("/configuration/{id}")
    @ApiOperation("通过系统配置项编号查找")
    public R getConfigurationById(@ApiParam(name = "对应系统配置项编号", required = true) @PathVariable Long id) {
        return R.ok().put("configuration", configurationService.getOne(new QueryWrapper<Configuration>().eq("id", id)));
    }

    /**
     * 添加系统配置处理
     *
     * @param configuration
     *            系统配置对象
     * @return 视图页面
     */
    @PostMapping("/configuration")
    @ApiOperation("添加系统配置处理")
    public R configurationCreateHandle(@ApiParam(name = "系统配置对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Configuration configuration) {
        if (!configurationService.save(configuration)) {
            throw new JsonException("添加系统配置失败！");
        }
        return R.ok();
    }

    /**
     * 修改系统配置处理
     *
     * @param configuration
     *            系统配置对象
     * @return 视图页面
     */
    @PutMapping("/configuration")
    @ApiOperation("修改系统配置处理")
    public R configurationModifyHandle(@ApiParam(name = "系统配置对象",
        required = true) @RequestBody @Validated(ModifyInputGroup.class) Configuration configuration) {
        if (!configurationService.updateById(configuration)) {
            throw new JsonException("修改系统配置失败！");
        }
        return R.ok();
    }

    /**
     * 通过系统配置编号删除
     *
     * @param id
     *            对应系统配置项编号
     * @return 是否成功
     */
    @DeleteMapping("/configuration/{id}")
    @ApiOperation("通过系统配置编号删除")
    public R removeConfigurationById(@ApiParam(name = "对应系统配置项编号", required = true) @PathVariable Long id) {
        if (configurationService.removeById(id)) {
            return R.ok("删除系统配置成功！");
        }
        throw new JsonException("删除系统配置失败！");
    }
}
