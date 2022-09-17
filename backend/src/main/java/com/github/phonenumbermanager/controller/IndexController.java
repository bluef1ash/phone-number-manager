package com.github.phonenumbermanager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.util.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
     * @return 视图页面
     */
    @GetMapping("/menu")
    @ApiOperation("获取首页菜单栏内容")
    public R getMenu() {
        SystemUser currentSystemUser =
            (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok(systemPermissionService.listMenu(currentSystemUser.getCompanies(), currentSystemUser.getId()));
    }
}
