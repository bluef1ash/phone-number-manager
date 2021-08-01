package com.github.phonenumbermanager.controller;

import com.github.phonenumbermanager.constant.ComputedDataTypes;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 首页控制器
 *
 * @author 廿二月的天
 */
@RestController
@RequestMapping("/index")
@Api(tags = "首页控制器")
public class IndexController extends BaseController {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private SubcontractorService subcontractorService;
    @Resource
    private UserPrivilegeService userPrivilegeService;

    /**
     * 获取首页菜单栏内容
     *
     * @param display 是否显示
     * @return 视图页面
     */
    @GetMapping("/getmenu")
    @ApiOperation("获取首页菜单栏内容")
    public Map<String, Object> getMenu(@ApiParam(name = "是否显示") Boolean display) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<UserPrivilege> systemUser = (Set<UserPrivilege>) authentication.getAuthorities();
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        //systemUser.getUserRoles().forEach(userRole -> userPrivileges.addAll(userRole.getUserPrivileges()));
        try {
            jsonMap.put("state", 1);
            jsonMap.put("userPrivileges", userPrivilegeService.get(display, userPrivileges));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 获取图表数据
     *
     * @param getType           需要获取的类型，null全部，1基本信息，2柱状图
     * @param companyId         需要获取的单位编号
     * @param companyType       需要获取的单位类型
     * @param barChartTypeParam 柱状图表类型参数
     * @return Ajax返回JSON对象
     */
    @GetMapping("/getcomputed")
    @ApiOperation("获取图表数据")
    public Map<String, Object> getComputedCount(@ApiParam(name = "需要获取的类型") Integer getType, @ApiParam(name = "需要获取的单位编号", required = true) Long companyId, @ApiParam(name = "需要获取的单位类型", required = true) Long companyType, @ApiParam(name = "柱状图表类型参数") Boolean barChartTypeParam) {
        getRoleId();
        Map<String, Object> jsonMap = new HashMap<>(3);
        Map<String, Object> resident = new HashMap<>(3);
        Map<String, Object> subcontractor = new HashMap<>(3);
        Map<String, Object> dormitory = new HashMap<>(3);
        try {
            if (getType == null || getType == 0) {
                resident.put("baseMessage", communityResidentService.get(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                resident.put("barChart", communityResidentService.get(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                subcontractor.put("barChart", subcontractorService.get(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                dormitory.put("baseMessage", dormitoryManagerService.get(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                dormitory.put("barChart", dormitoryManagerService.get(systemUser, companyId, companyType, barChartTypeParam, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_BASE_MESSAGE.getCode()) {
                resident.put("baseMessage", communityResidentService.get(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_BAR_CHART.getCode()) {
                resident.put("barChart", communityResidentService.get(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_SUBCONTRACTOR_BAR_CHART.getCode()) {
                subcontractor.put("barChart", subcontractorService.get(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.DORMITORY_BASE_MESSAGE.getCode()) {
                dormitory.put("baseMessage", dormitoryManagerService.get(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.DORMITORY_BAR_CHART.getCode()) {
                dormitory.put("barChart", dormitoryManagerService.get(systemUser, companyId, companyType, barChartTypeParam, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            }
            jsonMap.put("state", 1);
            jsonMap.put("resident", resident);
            jsonMap.put("subcontractor", subcontractor);
            jsonMap.put("dormitory", dormitory);
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }
}
