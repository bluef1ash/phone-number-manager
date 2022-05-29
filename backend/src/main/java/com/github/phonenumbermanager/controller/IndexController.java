package com.github.phonenumbermanager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.phonenumbermanager.constant.enums.MenuTypeEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
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
    private final CommunityResidentService communityResidentService;
    private final DormitoryManagerService dormitoryManagerService;
    private final SystemUserService systemUserService;
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
        List<String> components = new ArrayList<>();
        if (currentSystemUser.getCompanies() != null && !currentSystemUser.getCompanies().isEmpty()) {
            List<SystemPermission> systemPermissions = systemPermissionService.listByCompanyIds(
                currentSystemUser.getCompanies().stream().map(Company::getId).collect(Collectors.toList()));
            components = systemPermissions.stream()
                .filter(systemPermission -> MenuTypeEnum.FRONTEND.equals(systemPermission.getMenuType()))
                .map(SystemPermission::getFunctionName).collect(Collectors.toList());
        }
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("menuData", systemPermissionService.listMenu(display, currentSystemUser.getCompanies()));
        jsonMap.put("components", components);
        return R.ok(jsonMap);
    }
}
