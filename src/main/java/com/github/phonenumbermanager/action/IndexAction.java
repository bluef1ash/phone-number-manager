package com.github.phonenumbermanager.action;

import com.github.phonenumbermanager.constant.ComputedDataTypes;
import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.service.UserPrivilegeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 首页控制器
 *
 * @author 廿二月的天
 */
@Controller
public class IndexAction extends BaseAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private SubcontractorService subcontractorService;
    @Resource
    private UserPrivilegeService userPrivilegeService;

    /**
     * 登录后首页
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @GetMapping({"/", "/index"})
    public String index(HttpSession session, Model model) {
        getSessionRoleId(session);
        model.addAttribute("systemUser", systemUser);
        model.addAttribute("systemCompanyType", systemCompanyType);
        model.addAttribute("communityCompanyType", communityCompanyType);
        model.addAttribute("subdistrictCompanyType", subdistrictCompanyType);
        return "index/index";
    }

    /**
     * 使用AJAX技术获取首页菜单栏内容
     *
     * @param display 是否显示
     * @return 视图页面
     */
    @GetMapping("/index/getmenu")
    @ResponseBody
    public Map<String, Object> getMenu(Boolean display, HttpSession session) {
        Set<UserPrivilege> userPrivileges = (Set<UserPrivilege>) session.getAttribute("userPrivileges");
        try {
            Map<String, Object> jsonMap = new HashMap<>(3);
            jsonMap.put("state", 1);
            jsonMap.put("userPrivileges", userPrivilegeService.find(display, userPrivileges));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 使用AJAX技术获取图表数据
     *
     * @param session           session对象
     * @param getType           需要获取的类型，null全部，1基本信息，2柱状图
     * @param companyId         需要获取的单位编号
     * @param companyType       需要获取的单位类型
     * @param barChartTypeParam 柱状图表类型参数
     * @return Ajax返回JSON对象
     */
    @PostMapping("/index/getcomputedcount")
    @ResponseBody
    public Map<String, Object> getComputedCount(HttpSession session, Integer getType, Long companyId, Long companyType, Boolean barChartTypeParam) {
        getSessionRoleId(session);
        Map<String, Object> jsonMap = new HashMap<>(3);
        Map<String, Object> resident = new HashMap<>(3);
        Map<String, Object> subcontractor = new HashMap<>(3);
        Map<String, Object> dormitory = new HashMap<>(3);
        try {
            if (getType == null || getType == 0) {
                resident.put("baseMessage", communityResidentService.find(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                resident.put("barChart", communityResidentService.find(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                subcontractor.put("barChart", subcontractorService.find(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                dormitory.put("baseMessage", dormitoryManagerService.find(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
                dormitory.put("barChart", dormitoryManagerService.find(systemUser, companyId, companyType, barChartTypeParam, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_BASE_MESSAGE.getCode()) {
                resident.put("baseMessage", communityResidentService.find(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_BAR_CHART.getCode()) {
                resident.put("barChart", communityResidentService.find(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.RESIDENT_SUBCONTRACTOR_BAR_CHART.getCode()) {
                subcontractor.put("barChart", subcontractorService.find(systemUser, companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.DORMITORY_BASE_MESSAGE.getCode()) {
                dormitory.put("baseMessage", dormitoryManagerService.find(companyId, companyType, systemCompanyType, communityCompanyType, subdistrictCompanyType));
            } else if (getType == ComputedDataTypes.DORMITORY_BAR_CHART.getCode()) {
                dormitory.put("barChart", dormitoryManagerService.find(systemUser, companyId, companyType, barChartTypeParam, systemCompanyType, communityCompanyType, subdistrictCompanyType));
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
