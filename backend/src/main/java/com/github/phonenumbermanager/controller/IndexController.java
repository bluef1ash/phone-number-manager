package com.github.phonenumbermanager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.github.phonenumbermanager.constant.ComputedDataTypes;
import com.github.phonenumbermanager.constant.enums.MenuTypeEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.vo.ComputedVo;

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

    /**
     * 获取图表数据
     *
     *
     * @param computedVo
     *            计算视图对象
     * @return Ajax返回JSON对象
     */
    @PostMapping("/computed")
    @ApiOperation("获取图表数据")
    public R getComputedCount(@ApiParam(name = "计算视图对象") @RequestBody(required = false) ComputedVo computedVo) {
        getEnvironmentVariable();
        Map<String, Object> data = new HashMap<>(3);
        Map<String, Object> resident = new HashMap<>(3);
        Map<String, Object> user = new HashMap<>(3);
        Map<String, Object> dormitory = new HashMap<>(3);
        if (computedVo == null) {
            resident.put("baseMessage",
                communityResidentService.getBaseMessage(currentSystemUser.getCompanies(), null));
            resident.put("barChart", communityResidentService.getBarChart(currentSystemUser.getCompanies(), null));
            // user.put("barChart", systemUserService.getBarChart(currentSystemUser.getCompanies(),
            // null));
            dormitory.put("baseMessage",
                dormitoryManagerService.getBaseMessage(currentSystemUser.getCompanies(), null));
            dormitory.put("barChart", dormitoryManagerService.getBarChart(currentSystemUser.getCompanies(), null,
                computedVo.getBarChartTypeParam()));
        } else if (computedVo.getComputedType() == ComputedDataTypes.RESIDENT_BASE_MESSAGE.getCode()) {
            resident.put("baseMessage",
                communityResidentService.getBaseMessage(currentSystemUser.getCompanies(), computedVo.getCompanyIds()));
        } else if (computedVo.getComputedType() == ComputedDataTypes.RESIDENT_BAR_CHART.getCode()) {
            resident.put("barChart",
                communityResidentService.getBarChart(currentSystemUser.getCompanies(), computedVo.getCompanyIds()));
        } else if (computedVo.getComputedType() == ComputedDataTypes.RESIDENT_SUBCONTRACTOR_BAR_CHART.getCode()) {
            user.put("barChart",
                systemUserService.getBarChart(currentSystemUser.getCompanies(), computedVo.getCompanyIds()));
        } else if (computedVo.getComputedType() == ComputedDataTypes.DORMITORY_BASE_MESSAGE.getCode()) {
            dormitory.put("baseMessage",
                dormitoryManagerService.getBaseMessage(currentSystemUser.getCompanies(), computedVo.getCompanyIds()));
        } else if (computedVo.getComputedType() == ComputedDataTypes.DORMITORY_BAR_CHART.getCode()) {
            dormitory.put("barChart", dormitoryManagerService.getBarChart(currentSystemUser.getCompanies(),
                computedVo.getCompanyIds(), computedVo.getBarChartTypeParam()));
        } else {
            throw new JsonException("计算错误！");
        }
        data.put("resident", resident);
        data.put("currentSystemUser", user);
        data.put("dormitory", dormitory);
        return R.ok().put("data", data);
    }
}
