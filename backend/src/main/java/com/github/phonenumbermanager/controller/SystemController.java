package com.github.phonenumbermanager.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVo;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 系统管理控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/system")
@Api(tags = "系统管理控制器")
public class SystemController extends BaseController {
    private final ConfigurationService configurationService;

    /**
     * 系统配置列表
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统配置列表JSON
     */
    @GetMapping("/configuration")
    @ApiOperation("系统配置列表")
    public R configurationList(HttpServletRequest request, @ApiParam(name = "分页页码") Integer current,
        @ApiParam(name = "每页数据数量") Integer pageSize) {
        QueryWrapper<Configuration> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", configurationService.page(new Page<>(current, pageSize), wrapper));
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
        return R.ok().put("data",
            configurationService.getOne(new LambdaQueryWrapper<Configuration>().eq(Configuration::getId, id)));
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
        if (configurationService.save(configuration)) {
            refreshConfiguration();
            return R.ok();
        }
        throw new JsonException("添加系统配置失败！");
    }

    /**
     * 修改系统配置处理
     *
     * @param configuration
     *            系统配置对象
     * @return 视图页面
     */
    @PutMapping("/configuration/{id}")
    @ApiOperation("修改系统配置处理")
    public R configurationModifyHandle(@ApiParam(name = "对应系统配置项编号", required = true) @PathVariable Long id,
        @ApiParam(name = "系统配置对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) Configuration configuration) {
        configuration.setId(id).setVersion(configurationService
            .getOne(new LambdaQueryWrapper<Configuration>().eq(Configuration::getId, id)).getVersion());
        if (configurationService.updateById(configuration)) {
            refreshConfiguration();
            return R.ok();
        }
        throw new JsonException("修改系统配置失败！");
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
            refreshConfiguration();
            return R.ok("删除系统配置成功！");
        }
        throw new JsonException("删除系统配置失败！");
    }

    /**
     * 增删改批量操作
     *
     * @param batchRestfulVo
     *            批量操作视图对象
     * @return 是否成功
     */
    @PostMapping("/configuration/batch")
    @ApiOperation("增删改批量操作")
    public R configurationBatch(
        @ApiParam(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVo batchRestfulVo) {
        if (batchRestfulVo.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVo.getData(), Long.class);
            if (configurationService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("批量操作失败！");
    }

    /**
     * 刷新redis缓存
     */
    private void refreshConfiguration() {
        List<Configuration> configurations = configurationService.list();
        Map<String, Configuration> configurationMap =
            configurations.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
        redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, JSONUtil.toJsonStr(configurationMap));
    }
}
