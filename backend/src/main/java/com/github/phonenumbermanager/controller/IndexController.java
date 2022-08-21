package com.github.phonenumbermanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.util.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 首页控制器
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@RestController
@RequestMapping("/index")
@Api(tags = "首页控制器")
public class IndexController extends BaseController {
    private final SystemPermissionService systemPermissionService;

    /**
     * 获取首页菜单栏内容
     *
     * @param display
     *            是否显示
     * @return 视图页面
     */
    @GetMapping("/menu")
    @ApiOperation("获取首页菜单栏内容")
    public R getMenu(@ApiParam(name = "是否显示") Boolean display) {
        getEnvironmentVariable();
        return R.ok(systemPermissionService.listMenu(display, currentSystemUser.getCompanies()));
    }
}
