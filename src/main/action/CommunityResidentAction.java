package main.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import annotation.RefreshCsrfToken;
import main.validator.CommunityResidentInputValidator;
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
import exception.ParameterException;
import main.entity.Community;
import main.entity.CommunityResident;
import main.service.CommunityResidentService;
import main.service.CommunityService;
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
            binder.replaceValidators(new CommunityResidentInputValidator(communityResidentService));
        }
    }

    /**
     * 社区居民列表
     *
     * @param model
     * @param page
     * @param communityResident
     * @param unit
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String communityResidentList(Model model, Integer page, CommunityResident communityResident, String unit) {
        try {
            Map<String, Object> communityResidentMap = null;
            if ("GET".equals(request.getMethod())) {
                communityResidentMap = communityResidentService.findCommunityResidentsAndCommunity(page, null);
            } else {
                communityResidentMap = communityResidentService.findCommunityResidentByCommunityResident(communityResident, page, unit, null);
                model.addAttribute("communityResident", communityResident);
                model.addAttribute("unit", unit);
            }
            model.addAttribute("communityResidents", communityResidentMap.get("data"));
            model.addAttribute("pageInfo", communityResidentMap.get("pageInfo"));
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
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
            Map<String, Object> communityResidentMap = communityResidentService.findCommunityResidentAndCommunityById(id);
            List<Community> communities = communityService.findObjects();
            model.addAttribute("communities", communities);
            model.addAttribute("communityResident", communityResidentMap.get("communityResident"));
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "resident/edit";
    }

    /**
     * 添加、修改社区居民处理
     *
     * @param model
     * @param communityResident
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/handle", method = RequestMethod.POST)
    public String communityResidentCreateOrEditHandle(Model model, @Validated CommunityResident communityResident, BindingResult bindingResult) {
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
        if (communityResident.getCommunityResidentId() == null) {
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
    public @ResponseBody Map<String, Object> deleteCommunityResidentForAjax(String id) {
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
    public @ResponseBody
    Map<String, Object> communityResidentImportAsSystem() {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
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
            throw new BusinessException("上传文件失败！", e);
        }
    }

    /**
     * 导出居民信息到Excel
     *
     * @param session
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void communityResidentSaveAsExcel(HttpSession session) {

    }
}
