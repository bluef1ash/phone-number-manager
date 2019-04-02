package www.action;

import annotation.RefreshCsrfToken;
import annotation.SystemUserAuth;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import exception.BusinessException;
import exception.JsonException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CommonUtil;
import utils.CsrfTokenUtil;
import utils.ExcelUtil;
import www.entity.Community;
import www.entity.CommunityResident;
import www.entity.SystemUser;
import www.service.CommunityResidentService;
import www.service.CommunityService;
import www.validator.CommunityResidentInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区居民控制器
 *
 * @author 廿二月的天
 */
@SystemUserAuth
@Controller
@RequestMapping("/resident")
public class CommunityResidentAction extends BaseAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private CommunityService communityService;
    private final HttpServletRequest request;

    @Autowired
    public CommunityResidentAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("communityResidentCreateOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new CommunityResidentInputValidator(communityResidentService, request));
        }
    }

    /**
     * 社区居民列表
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String communityResidentList(HttpSession session, Model model) {
        setPersonVariable(session, model);
        try {
            Map<String, Object> communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, null, null);
            model.addAttribute("communityResidents", communityResidentMap.get("data"));
            model.addAttribute("pageInfo", communityResidentMap.get("pageInfo"));
            model.addAttribute("dataType", 0);
            return "resident/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加社区居民
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createCommunityResident(HttpSession session, Model model) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        try {
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser, configurationsMap);
            model.addAttribute("communities", JSON.toJSON(communities));
            return "resident/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑社区居民
     *
     * @param session session对象
     * @param model   前台模型
     * @param id      需要编辑的社区居民的编号
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editCommunityResident(HttpSession session, Model model, Long id) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        try {
            CommunityResident communityResident = communityResidentService.findCommunityResidentAndCommunityById(id);
            model.addAttribute("communityResident", communityResident);
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser, configurationsMap);
            model.addAttribute("communities", communities);
            return "resident/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改社区居民处理
     *
     * @param httpServletRequest HTTP响应对象
     * @param session            Session对象
     * @param model              前台模型
     * @param communityResident  前台传递的社区居民对象
     * @param bindingResult      错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    @VerifyCSRFToken
    public String communityResidentCreateOrEditHandle(HttpServletRequest httpServletRequest, HttpSession session, Model model, @Validated CommunityResident communityResident, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            try {
                throwsError(communityService, model, bindingResult);
                return "resident/edit";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("系统出现错误，请联系管理员！");
            }
        }
        if ("POST".equals(httpServletRequest.getMethod())) {
            try {
                communityResidentService.createCommunityResident(communityResident);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加社区居民失败！", e);
            }
        } else {
            try {
                communityResidentService.updateCommunityResident(communityResident);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改社区居民失败！", e);
            }
        }
        return "redirect:/resident/list";
    }

    /**
     * 使用AJAX技术通过社区居民ID删除社区居民
     *
     * @param session session对象
     * @param id      对应编号
     * @return Ajax信息
     */
    @RequestMapping(value = "/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> deleteCommunityResidentForAjax(HttpSession session, Long id) {
        Map<String, Object> jsonMap = new HashMap<>(4);
        try {
            communityResidentService.deleteObjectById(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区居民成功！");
            jsonMap.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除社区居民失败！", e);
        }
    }

    /**
     * 导入居民信息进系统
     *
     * @param request       HTTP请求对象
     * @param session       session对象
     * @param subdistrictId 导入的街道编号
     * @return Ajax信息
     */
    @SystemUserAuth(enforce = true)
    @RequestMapping(value = "/import_as_system", method = RequestMethod.POST)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> communityResidentImportAsSystem(HttpSession session, HttpServletRequest request, Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            Workbook workbook = uploadExcel(request, session, "excel_resident_title");
            jsonMap.put("state", communityResidentService.addCommunityResidentFromExcel(workbook, subdistrictId, configurationsMap));
            jsonMap.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("上传文件失败！", e);
        }
    }

    /**
     * 导出居民信息到Excel
     *
     * @param session  session对象
     * @param response 前台响应对象
     * @param data     传递数据
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response, String data) {
        List<Map<String, Object>> userData = getUserData(session, data);
        //获取属性-列头
        Map<String, String> headMap = communityResidentService.getPartStatHead();
        String excelResidentTitleUp = CommonUtil.convertConfigurationString(configurationsMap.get("excel_resident_title_up"));
        String excelResidentTitle = CommonUtil.convertConfigurationString(configurationsMap.get("excel_resident_title"));
        try {
            ExcelUtil.DataHandler dataHandler = communityResidentService.setExcelHead(new String[]{excelResidentTitleUp, excelResidentTitle});
            // 获取业务数据集
            JSONArray dataJson = communityResidentService.findCommunityResidentsAndCommunitiesBySystemUserId(configurationsMap, userData);
            ByteArrayOutputStream byteArrayOutputStream = ExcelUtil.exportExcelX(headMap, dataJson, 0, dataHandler);
            ExcelUtil.downloadExcelFile(response, request, excelResidentTitle, byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            setCookieError(request, response);
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术加载社区居民列表
     *
     * @param session       Session对象
     * @param page          分页页码
     * @param companyId     单位编号
     * @param companyRoleId 单位类别编号
     * @param name          社区居民姓名
     * @param address       社区居民家庭地址
     * @param phone         社区居民联系方式
     * @param isSearch      是否搜索
     * @return Ajax消息
     */
    @RequestMapping(value = "/ajax_list", method = RequestMethod.GET)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> findCommunityResidentsForAjax(HttpSession session, Integer page, Long companyId, Long companyRoleId, String name, String address, String phone, Boolean isSearch) {
        getSessionRoleId(session);
        Map<String, Object> communityResidentMap;
        try {
            if (isSearch != null && isSearch) {
                CommunityResident communityResident = new CommunityResident();
                communityResident.setCommunityResidentName(name);
                communityResident.setCommunityResidentAddress(address);
                communityResident.setCommunityResidentPhones(phone);
                communityResidentMap = communityResidentService.findCommunityResidentByCommunityResident(systemUser, communityRoleId, subdistrictRoleId, systemAdministratorId, communityResident, companyId, companyRoleId, page, null);
            } else {
                communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, page, null);
            }
            return setJsonMap(session, communityResidentMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("查找社区居民失败！", e);
        }
    }
}
