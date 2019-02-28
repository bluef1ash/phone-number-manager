package www.action;

import annotation.RefreshCsrfToken;
import annotation.SystemUserAuth;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import exception.BusinessException;
import exception.JsonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import utils.CommonUtil;
import utils.CsrfTokenUtil;
import utils.ExcelUtil;
import www.entity.Community;
import www.entity.CommunityResident;
import www.entity.SystemUser;
import www.service.CommunityResidentService;
import www.service.CommunityService;
import www.service.SubcontractorService;
import www.service.SubdistrictService;
import www.validator.CommunityResidentInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
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
public class CommunityResidentAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private CommunityService communityService;
    @Resource
    private SubdistrictService subdistrictService;
    @Resource
    private SubcontractorService subcontractorService;
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
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        Long systemRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_role_id"));
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        try {
            Map<String, Object> communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, null, null);
            model.addAttribute("communityResidents", communityResidentMap.get("data"));
            model.addAttribute("pageInfo", communityResidentMap.get("pageInfo"));
            Map<String, Long> roleLocationIdMap = new HashMap<>(4);
            roleLocationIdMap.put("systemRoleId", systemRoleId);
            roleLocationIdMap.put("communityRoleId", communityRoleId);
            roleLocationIdMap.put("subdistrictRoleId", subdistrictRoleId);
            model.addAttribute("roleLocationIds", roleLocationIdMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "resident/list";
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
     * @param model              前台模型
     * @param communityResident  前台传递的社区居民对象
     * @param bindingResult      错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    @VerifyCSRFToken
    public String communityResidentCreateOrEditHandle(HttpServletRequest httpServletRequest, Model model, @Validated CommunityResident communityResident, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<Community> communities = communityService.findObjects();
                model.addAttribute("communities", communities);
                // 输出错误信息
                List<ObjectError> allErrors = bindingResult.getAllErrors();
                model.addAttribute("messageErrors", allErrors);
                return "resident/edit";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统出现错误，请联系管理员！");
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
        Map<String, Object> map = new HashMap<>(4);
        try {
            communityResidentService.deleteObjectById(id);
            map.put("state", 1);
            map.put("message", "删除社区居民成功！");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除社区居民失败！", e);
        }
    }

    /**
     * 导入居民信息进系统
     *
     * @param httpServletRequest HTTP请求对象
     * @param session            session对象
     * @param subdistrictId      导入的街道编号
     * @return Ajax信息
     */
    @SystemUserAuth(enforce = true)
    @RequestMapping(value = "/import_as_system", method = RequestMethod.POST)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> communityResidentImportAsSystem(HttpServletRequest httpServletRequest, HttpSession session, Long subdistrictId) {
        Map<String, Object> map = new HashMap<>(4);
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
            MultipartFile file = multipartRequest.getFile("file");
            if (file == null || file.isEmpty()) {
                throw new JsonException("文件不存在！");
            }
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
            if (StringUtils.isEmpty(filename)) {
                filename = CommonUtil.convertConfigurationString(configurationsMap.get("excel_title"));
            }
            Workbook workbook = ExcelUtil.getWorkbook(inputStream, filename);
            if (workbook == null) {
                throw new Exception("创建Excel工作薄为空！");
            }
            int state = communityResidentService.addCommunityResidentFromExcel(workbook, subdistrictId, configurationsMap);
            map.put("state", state);
            return map;
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
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response) {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        try {
            // 获取业务数据集
            JSONArray data = communityResidentService.findCommunityResidentsAndCommunitiesBySystemUserId(configurationsMap, systemUser.getRoleId(), systemUser.getRoleLocationId());
            //获取属性-列头
            Map<String, String> headMap = communityResidentService.getPartStatHead();
            String excelTitleUp = CommonUtil.convertConfigurationString(configurationsMap.get("excel_title_up"));
            String excelTitle = CommonUtil.convertConfigurationString(configurationsMap.get("excel_title"));
            ExcelUtil.downloadExcelFile(excelTitleUp, excelTitle, headMap, data, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术加载社区居民列表
     *
     * @param session           Session对象
     * @param page              分页页码
     * @param companyId         单位编号
     * @param companyLocationId 单位类别
     * @param residentName      社区居民姓名
     * @param residentAddress   社区居民家庭地址
     * @param residentPhone     社区居民联系方式
     * @param isSearch          是否搜索
     * @return Ajax消息
     */
    @RequestMapping(value = "/ajax_list", method = RequestMethod.GET)
    @VerifyCSRFToken
    @ResponseBody
    public Map<String, Object> findCommunityResidentsForAjax(HttpSession session, Integer page, Long companyId, Long companyLocationId, String residentName, String residentAddress, String residentPhone, Boolean isSearch) {
        Map<String, Object> map = new HashMap<>(5);
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        @SuppressWarnings("unchecked") Map<String, Object> configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        Long systemRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_role_id"));
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        Map<String, Object> communityResidentMap;
        try {
            if (isSearch != null && isSearch) {
                CommunityResident communityResident = new CommunityResident();
                communityResident.setCommunityResidentName(residentName);
                communityResident.setCommunityResidentAddress(residentAddress);
                communityResident.setCommunityResidentPhones(residentPhone);
                communityResidentMap = communityResidentService.findCommunityResidentByCommunityResident(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, systemAdministratorId, communityResident, companyId, companyLocationId, page, null);
            } else {
                communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, page, null);
            }
            map.put("state", 1);
            map.put("communityResidents", communityResidentMap.get("data"));
            map.put("pageInfo", communityResidentMap.get("pageInfo"));
            map.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("查找社区居民失败！", e);
        }
    }
}
