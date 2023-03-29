package com.github.phonenumbermanager.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.BatchRestfulMethod;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.vo.BatchRestfulVO;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

/**
 * 系统管理控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/system")
@Tag(name = "系统管理控制器")
public class SystemController extends BaseController {
    private final ConfigurationService configurationService;

    /**
     * 获取系统配置分页列表
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据
     * @return 系统配置分页集合对象
     */
    @GetMapping("/configuration")
    @Operation(summary = "系统配置列表")
    public R configurationList(HttpServletRequest request, @Parameter(name = "分页页码") Integer current,
        @Parameter(name = "每页数据数量") Integer pageSize) {
        QueryWrapper<Configuration> wrapper = new QueryWrapper<>();
        getSearchWrapper(request, wrapper);
        return R.ok().put("data", configurationService.page(new Page<>(current, pageSize), wrapper));
    }

    /**
     * 通过系统配置项编号查找系统配置项详细信息
     *
     * @param id
     *            系统配置项编号
     * @return 对应编号的系统配置项的详细信息
     */
    @GetMapping("/configuration/{id}")
    @Operation(summary = "通过系统配置项编号查找系统配置项详细信息")
    public R getConfigurationById(@Parameter(name = "系统配置项编号", required = true) @PathVariable Long id) {
        return R.ok().put("data",
            configurationService.getOne(new LambdaQueryWrapper<Configuration>().eq(Configuration::getId, id)));
    }

    /**
     * 添加系统配置处理
     *
     * @param configuration
     *            添加的系统配置对象
     * @return 添加成功或者失败
     */
    @PostMapping("/configuration")
    @Operation(summary = "添加系统配置处理")
    public R configurationCreateHandle(@Parameter(name = "系统配置对象",
        required = true) @RequestBody @Validated(CreateInputGroup.class) Configuration configuration) {
        if (configurationService.save(configuration)) {
            return R.ok();
        }
        throw new JsonException("添加系统配置失败！");
    }

    /**
     * 修改系统配置处理
     *
     * @param configuration
     *            系统配置对象
     * @return 修改成功或者失败
     */
    @PutMapping("/configuration/{id}")
    @Operation(summary = "修改系统配置处理")
    public R configurationModifyHandle(@Parameter(name = "对应系统配置项编号", required = true) @PathVariable Long id,
        @Parameter(name = "系统配置对象",
            required = true) @RequestBody @Validated(ModifyInputGroup.class) Configuration configuration) {
        configuration.setId(id).setVersion(configurationService
            .getOne(new LambdaQueryWrapper<Configuration>().eq(Configuration::getId, id)).getVersion());
        if (configurationService.updateById(configuration)) {
            return R.ok();
        }
        throw new JsonException("修改系统配置失败！");
    }

    /**
     * 通过系统配置编号删除
     *
     * @param id
     *            对应系统配置项编号
     * @return 删除成功或者失败
     */
    @DeleteMapping("/configuration/{id}")
    @Operation(summary = "通过系统配置编号删除")
    public R removeConfigurationById(@Parameter(name = "对应系统配置项编号", required = true) @PathVariable Long id) {
        if (configurationService.removeById(id)) {
            return R.ok("删除系统配置成功！");
        }
        throw new JsonException("删除系统配置失败！");
    }

    /**
     * 系统配置增删改批量操作
     *
     * @param batchRestfulVO
     *            批量操作视图对象
     * @return 批量操作成功或者失败
     */
    @PostMapping("/configuration/batch")
    @Operation(summary = "系统配置增删改批量操作")
    public R configurationBatch(
        @Parameter(name = "批量操作视图对象", required = true) @RequestBody @Validated BatchRestfulVO batchRestfulVO) {
        if (batchRestfulVO.getMethod() == BatchRestfulMethod.DELETE) {
            List<Long> ids = JSONUtil.toList(batchRestfulVO.getData(), Long.class);
            if (configurationService.removeByIds(ids)) {
                return R.ok();
            }
        }
        throw new JsonException("系统配置批量操作失败！");
    }
}
