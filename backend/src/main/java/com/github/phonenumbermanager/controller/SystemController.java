package com.github.phonenumbermanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.validator.ConfigurationInputValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod())) {
            binder.replaceValidators(new ConfigurationInputValidator(configurationService, request));
        }
    }

    /**
     * 系统配置列表
     *
     * @param page  分页页码
     * @param limit 每页数据
     * @return 系统配置列表JSON
     */
    @GetMapping("/configuration")
    @ApiOperation("系统配置列表")
    public Map<String, Object> configurationList(@ApiParam(name = "分页页码") Integer page, @ApiParam(name = "每页数据数量") Integer limit) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (page == null) {
            jsonMap.put("configurations", configurationService.list());
        } else {
            Page<Configuration> configurationPage = new Page<>(page, limit);
            jsonMap.put("configurations", configurationService.page(configurationPage, null));
        }
        return jsonMap;
    }

    /**
     * 通过系统配置项KEY查找
     *
     * @param key 对应系统配置项关键字
     * @return 系统配置项
     */
    @GetMapping("/configuration/{key}")
    @ApiOperation("通过系统配置项KEY查找")
    public Map<String, Object> editConfiguration(@ApiParam(name = "对应系统配置项关键字", required = true) @PathVariable String key) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        jsonMap.put("configuration", configurationService.getById(key));
        return jsonMap;
    }

    /**
     * 添加、修改系统配置处理
     *
     * @param request       HTTP请求对象
     * @param configuration 系统配置对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/configuration", method = {RequestMethod.POST, RequestMethod.PUT})
    @ApiOperation("添加、修改系统配置处理")
    public Map<String, Object> configurationCreateOrEditHandle(HttpServletRequest request, @ApiParam(name = "系统配置对象", required = true) @RequestBody @Validated Configuration configuration, BindingResult bindingResult) {
        Map<String, Object> jsonMap = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            jsonMap.put("messageErrors", bindingResult.getAllErrors());
            return jsonMap;
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (!configurationService.save(configuration)) {
                throw new JsonException("添加系统配置失败！");
            }
        } else {
            if (!configurationService.updateById(configuration)) {
                throw new JsonException("修改系统配置失败！");
            }
        }
        jsonMap.put("state", 1);
        return jsonMap;
    }

    /**
     * 通过系统配置编号删除
     *
     * @param key 对应系统配置项关键字
     * @return Ajax信息
     */
    @DeleteMapping("/configuration/{key}")
    @ApiOperation("通过系统配置编号删除")
    public Map<String, Object> deleteConfigurationForAjax(@ApiParam(name = "对应系统配置项关键字", required = true) @PathVariable String key) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        Map<String, Object> columnMap = new HashMap<>(1);
        columnMap.put("key", key);
        if (configurationService.removeByMap(columnMap)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除系统配置成功！");
            return jsonMap;
        }
        throw new JsonException("删除系统配置失败！");
    }
}
