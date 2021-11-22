package com.github.phonenumbermanager.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.phonenumbermanager.constant.ComputedDataTypes;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 首页控制器
 *
 * @author 廿二月的天
 */
@Slf4j
@RestController
@RequestMapping("/index")
@Api(tags = "首页控制器")
public class IndexController extends BaseController {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private SystemPermissionService systemPermissionService;

    /**
     * 获取首页菜单栏内容
     *
     * @param display
     *            是否显示
     * @return 视图页面
     */
    @GetMapping("/get-menu")
    @ApiOperation("获取首页菜单栏内容")
    public R getMenu(@ApiParam(name = "是否显示") Boolean display) {
        SystemUser userDetails = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok().put("systemPermissions", systemPermissionService.listMenu(display, userDetails.getCompanies()));
    }

    /**
     * 获取图表数据
     *
     * @param getType
     *            需要获取的类型，null全部，1基本信息，2柱状图
     * @param companyId
     *            需要获取的单位编号
     * @param barChartTypeParam
     *            柱状图表类型参数
     * @return Ajax返回JSON对象
     */
    @GetMapping("/get-computed")
    @ApiOperation("获取图表数据")
    public R getComputedCount(@ApiParam(name = "需要获取的类型") Integer getType, @ApiParam(name = "需要获取的单位编号") Long companyId,
        @ApiParam(name = "柱状图表类型参数") Boolean barChartTypeParam) {
        getEnvironmentVariable();
        Map<String, Object> resident = new HashMap<>(3);
        Map<String, Object> user = new HashMap<>(3);
        Map<String, Object> dormitory = new HashMap<>(3);
        if (getType == ComputedDataTypes.RESIDENT_BASE_MESSAGE.getCode()) {
            resident.put("baseMessage", communityResidentService.getBaseMessage(systemUser.getCompanies(), companyId));
        } else if (getType == ComputedDataTypes.RESIDENT_BAR_CHART.getCode()) {
            resident.put("barChart", communityResidentService.getBarChart(systemUser.getCompanies(), companyId));
        } else if (getType == ComputedDataTypes.RESIDENT_SUBCONTRACTOR_BAR_CHART.getCode()) {
            user.put("barChart", systemUserService.getBarChart(systemUser.getCompanies(), companyId));
        } else if (getType == ComputedDataTypes.DORMITORY_BASE_MESSAGE.getCode()) {
            dormitory.put("baseMessage", dormitoryManagerService.getBaseMessage(systemUser.getCompanies(), companyId));
        } else if (getType == ComputedDataTypes.DORMITORY_BAR_CHART.getCode()) {
            dormitory.put("barChart",
                dormitoryManagerService.getBarChart(systemUser.getCompanies(), companyId, barChartTypeParam));
        } else {
            resident.put("baseMessage", communityResidentService.getBaseMessage(systemUser.getCompanies(), companyId));
            resident.put("barChart", communityResidentService.getBarChart(systemUser.getCompanies(), companyId));
            user.put("barChart", systemUserService.getBarChart(systemUser.getCompanies(), companyId));
            dormitory.put("baseMessage", dormitoryManagerService.getBaseMessage(systemUser.getCompanies(), companyId));
            dormitory.put("barChart",
                dormitoryManagerService.getBarChart(systemUser.getCompanies(), companyId, barChartTypeParam));
        }
        return Objects.requireNonNull(Objects.requireNonNull(R.ok().put("resident", resident)).put("systemUser", user))
            .put("dormitory", dormitory);
    }
}
