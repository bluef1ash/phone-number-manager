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
import exception.CsrfException;
import www.entity.SystemUser;
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
import www.entity.Community;
import www.entity.CommunityResident;
import www.service.CommunityResidentService;
import www.service.CommunityService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import utils.ExcelUtil;

/**
 * 社区居民控制器
 */
@SystemUserAuth
@Controller
@RequestMapping("/resident")
public class CommunityResidentAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private CommunityService communityService;
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
     * @param model
     * @param page
     * @param communityResident
     * @param company
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String communityResidentList(HttpServletRequest httpServletRequest, Model model, Integer page, CommunityResident communityResident, String company) {
        try {
            Map<String, Object> communityResidentMap;
            String queryString = httpServletRequest.getQueryString();
            if (queryString == null || !queryString.contains("communityResidentName")) {
                communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(page, null);
            } else {
                queryString = queryString.replaceAll("page=\\d+&", "");
                communityResidentMap = communityResidentService.findCommunityResidentByCommunityResident(communityResident, company, page, null);
                model.addAttribute("communityResident", communityResident);
            }
            Map<String, Object> dataAndPagination = (Map<String, Object>) communityResidentMap.get("dataAndPagination");
            model.addAttribute("communityResidents", dataAndPagination.get("data"));
            model.addAttribute("pageInfo", dataAndPagination.get("pageInfo"));
            model.addAttribute("count", communityResidentMap.get("count"));
            model.addAttribute("queryString", queryString);
            model.addAttribute("company", company);
        } catch (CsrfException e1) {
            throw new CsrfException(e1.getMessage(), e1);
        } catch (Exception e2) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e2);
        }
        return "resident/list";
    }

    /**
     * 添加社区居民
     *
     * @param model
     * @return
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createCommunityResident(Model model) {
        try {
            List<Community> communities = communityService.findObjects();
            model.addAttribute("communities", communities);
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "resident/create";
    }

    /**
     * 编辑社区居民
     *
     * @param model
     * @param id
     * @return
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editCommunityResident(Model model, Integer id) {
        try {
            CommunityResident communityResident = communityResidentService.findCommunityResidentAndCommunityById(id);
            List<Community> communities = communityService.findObjects();
            model.addAttribute("communities", communities);
            model.addAttribute("communityResident", communityResident);
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "resident/edit";
    }

    /**
     * 添加、修改社区居民处理
     *
     * @param httpServletRequest
     * @param model
     * @param communityResident
     * @param bindingResult
     * @return
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
     * @param id
     * @return
     */
    @RequestMapping(value = "/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteCommunityResidentForAjax(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            communityResidentService.deleteObjectById(Integer.parseInt(id));
            map.put("state", 1);
            map.put("message", "删除社区居民成功！");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", 0);
            map.put("message", "删除社区居民失败！");
        }
        return map;
    }

    /**
     * 导入居民信息进系统
     *
     * @return
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
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", state);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("上传文件失败！", e);
        }
    }

    /**
     * 导出居民信息到Excel
     *
     * @param session
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response) {
        try {
            SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
            JSONArray ja = communityResidentService.findCommunityResidentsAndCommunitiesBySystemUserId(systemUser.getRoleId(), systemUser.getRoleLocationId());//获取业务数据集
            Map<String, String> headMap = communityResidentService.getPartStatHead();//获取属性-列头
            String title = "“评社区”活动电话库登记表";
            ExcelUtil.downloadExcelFile(title, headMap, ja, response);
        } catch (Exception e) {
            throw new BusinessException("导出Excel文件失败！");
        }
    }
}
