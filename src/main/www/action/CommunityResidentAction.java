package www.action;

import java.io.InputStream;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import com.alibaba.fastjson.JSONArray;
import www.entity.*;
import www.service.SubdistrictService;
import www.validator.CommunityResidentInputValidator;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import annotation.SystemUserAuth;
import exception.BusinessException;
import www.service.CommunityResidentService;
import www.service.CommunityService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import utils.ExcelUtil;

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
    @Autowired
    private HttpServletRequest request;

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
     * @param httpSession        session对象
     * @param httpServletRequest HTTP请求对象
     * @param model              前台模型
     * @param page               分页对象
     * @param communityResident  前台传过来的社区居民对象
     * @param company            单位
     * @return 视图页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String communityResidentList(HttpSession httpSession, HttpServletRequest httpServletRequest, Model model, Integer page, CommunityResident communityResident, String company) {
        try {
            Map<String, Object> communityResidentMap;
            String queryString = httpServletRequest.getQueryString();
            SystemUser systemUser = (SystemUser) httpSession.getAttribute("systemUser");
            if (queryString == null || !queryString.contains("communityResidentName")) {
                communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(systemUser, page, null);
            } else {
                queryString = queryString.replaceAll("page=\\d+&", "");
                communityResidentMap = communityResidentService.findCommunityResidentByCommunityResident(systemUser, communityResident, company, page, null);
                model.addAttribute("communityResident", communityResident);
            }
            Map<String, Object> dataAndPagination = (Map<String, Object>) communityResidentMap.get("dataAndPagination");
            model.addAttribute("communityResidents", dataAndPagination.get("data"));
            model.addAttribute("pageInfo", dataAndPagination.get("pageInfo"));
            model.addAttribute("count", communityResidentMap.get("count"));
            model.addAttribute("queryString", queryString);
            model.addAttribute("company", company);
        } catch (Exception e) {
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
        try {
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser);
            model.addAttribute("communities", communities);
            return "resident/edit";
        } catch (Exception e) {
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
    public String editCommunityResident(HttpSession session, Model model, Integer id) {
        try {
            CommunityResident communityResident = communityResidentService.findCommunityResidentAndCommunityById(id);
            model.addAttribute("communityResident", communityResident);
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser);
            model.addAttribute("communities", communities);
            return "resident/edit";
        } catch (Exception e) {
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
            throw new BusinessException("系统出现错误，请联系管理员！");
        }
        if ("POST".equals(httpServletRequest.getMethod())) {
            try {
                communityResidentService.createCommunityResident(communityResident);
            } catch (Exception e) {
                throw new BusinessException("添加社区居民失败！", e);
            }
        } else {
            try {
                communityResidentService.updateCommunityResident(communityResident);
            } catch (Exception e) {
                throw new BusinessException("修改社区居民失败！", e);
            }
        }
        return "redirect:/resident/list.action";
    }

    /**
     * 使用AJAX技术通过社区居民ID删除社区居民
     *
     * @param id 对应编号
     * @return 视图页面
     */
    @RequestMapping(value = "/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteCommunityResidentForAjax(String id) {
        Map<String, Object> map = new HashMap<>(3);
        try {
            communityResidentService.deleteObjectById(Integer.parseInt(id));
            map.put("state", 1);
            map.put("message", "删除社区居民成功！");
        } catch (Exception e) {
            map.put("state", 0);
            map.put("message", "删除社区居民失败！");
        }
        return map;
    }

    /**
     * 导入居民信息进系统
     *
     * @return Ajax信息
     */
    @SystemUserAuth(enforce = true)
    @RequestMapping(value = "/import_as_system", method = RequestMethod.POST)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> communityResidentImportAsSystem(HttpServletRequest httpServletRequest) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
            MultipartFile file = multipartRequest.getFile("file");
            if (file.isEmpty()) {
                throw new BusinessException("文件不存在！");
            }
            InputStream inputStream = file.getInputStream();
            Workbook workbook = ExcelUtil.getWorkbook(inputStream, file.getOriginalFilename());
            if (workbook == null) {
                throw new Exception("创建Excel工作薄为空！");
            }
            int state = communityResidentService.addCommunityResidentFromExcel(workbook);
            Map<String, Object> map = new HashMap<>(2);
            map.put("state", state);
            return map;
        } catch (Exception e) {
            throw new BusinessException("上传文件失败！", e);
        }
    }

    /**
     * 导出居民信息到Excel
     *
     * @param session session对象
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response) {
        try {
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            // 获取业务数据集
            JSONArray data = communityResidentService.findCommunityResidentsAndCommunitiesBySystemUserId(systemUser.getRoleId(), systemUser.getRoleLocationId());
            //获取属性-列头
            Map<String, String> headMap = communityResidentService.getPartStatHead();
            String title = "“评社区”活动电话库登记表";
            ExcelUtil.downloadExcelFile(title, headMap, data, response);
        } catch (Exception e) {
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术列出社区居委会
     *
     * @return Ajax消息
     */
    @RequestMapping(value = "/ajax_select", method = RequestMethod.GET)
    @VerifyCSRFToken
    public @ResponseBody
    Set<TreeMenu> findCommunityForAjax(HttpSession session) {
        try {
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            Integer roleId = systemUser.getUserRole().getRoleId();
            Integer roleLocationId = systemUser.getRoleLocationId();
            Set<Subdistrict> subdistricts = subdistrictService.findCommunitiesAndSubdistrictsByRole(roleId, roleLocationId);
            Set<TreeMenu> treeMenus = new HashSet<>();
            for (Subdistrict subdistrict : subdistricts) {
                TreeMenu treeMenu = new TreeMenu();
                treeMenu.setId(subdistrict.getSubdistrictId());
                treeMenu.setName(subdistrict.getSubdistrictName());
                if (treeMenus.size() == 0) {
                    treeMenu.setSpread(true);
                }
                List<TreeMenu> children = new ArrayList<>();
                for (Community community : subdistrict.getCommunities()) {
                    TreeMenu childrenTree = new TreeMenu();
                    childrenTree.setId(community.getCommunityId());
                    childrenTree.setName(community.getCommunityName());
                    children.add(childrenTree);
                }
                treeMenu.setChildren(children);
                treeMenus.add(treeMenu);
            }
            return treeMenus;
        } catch (Exception e) {
            throw new BusinessException("查找社区失败！", e);
        }
    }
}
