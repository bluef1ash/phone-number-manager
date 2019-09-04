package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.ExcelUtils;
import com.github.phonenumbermanager.validator.CommunityResidentInputValidator;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Controller
@RequestMapping("/resident")
public class CommunityResidentAction extends BaseAction {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private CommunityService communityService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        String url = requestUrl.substring(requestUrl.lastIndexOf("/"));
        if (VALID_URL.equals(url)) {
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
    @GetMapping("/list")
    public String communityResidentList(HttpSession session, Model model) {
        setPersonVariable(session, model);
        try {
            Map<String, Object> communityResidentMap = communityResidentService.findCorrelation(systemUser, systemCompanyType, communityCompanyType, subdistrictCompanyType, null, null);
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
    @GetMapping("/create")
    public String createCommunityResident(HttpSession session, Model model) {
        getSessionRoleId(session);
        try {
            List<Community> communities = communityService.find(systemUser, communityCompanyType, subdistrictCompanyType);
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
    @GetMapping("/edit")
    public String editCommunityResident(HttpSession session, Model model, Long id) {
        getSessionRoleId(session);
        try {
            CommunityResident communityResident = communityResidentService.findCorrelation(id);
            model.addAttribute("communityResident", communityResident);
            List<Community> communities = communityService.find(systemUser, communityCompanyType, subdistrictCompanyType);
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
     * @param httpServletRequest HTTP请求对象
     * @param session            Session对象
     * @param model              前台模型
     * @param communityResident  前台传递的社区居民对象
     * @param bindingResult      错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
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
        if (RequestMethod.POST.toString().equals(httpServletRequest.getMethod())) {
            try {
                communityResidentService.create(communityResident);
            } catch (BusinessException be) {
                be.printStackTrace();
                throw new BusinessException(be.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加社区居民失败！", e);
            }
        } else {
            try {
                communityResidentService.update(communityResident);
            } catch (BusinessException be) {
                be.printStackTrace();
                throw new BusinessException(be.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改社区居民失败！", e);
            }
        }
        return "redirect:/resident/list";
    }

    /**
     * 使用AJAX技术通过社区居民编号删除社区居民
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteCommunityResidentForAjax(Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            communityResidentService.delete(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区居民成功！");
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
    @PostMapping("/import_as_system")
    @ResponseBody
    public Map<String, Object> communityResidentImportAsSystem(HttpSession session, HttpServletRequest request, Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            Workbook workbook = uploadExcel(request, session, "excel_resident_title");
            jsonMap.put("state", communityResidentService.create(workbook, subdistrictId, configurationsMap));
            return jsonMap;
        } catch (BusinessException be) {
            be.printStackTrace();
            throw new JsonException(be.getMessage());
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
    @GetMapping("/save_as_excel")
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response, String data) {
        List<Map<String, Object>> userData = getDecodeData(session, data);
        //获取属性-列头
        Map<String, String> headMap = communityResidentService.findPartStatHead();
        String excelResidentTitleUp = CommonUtils.convertConfigurationString(configurationsMap.get("excel_resident_title_up"));
        String excelResidentTitle = CommonUtils.convertConfigurationString(configurationsMap.get("excel_resident_title"));
        ExcelUtils.DataHandler dataHandler = communityResidentService.setExcelHead(new String[]{excelResidentTitleUp, excelResidentTitle});
        try {
            // 获取业务数据集
            JSONArray dataJson = communityResidentService.findCorrelation(communityCompanyType, subdistrictCompanyType, userData);
            ByteArrayOutputStream byteArrayOutputStream = ExcelUtils.exportExcelX(excelResidentTitle, headMap, dataJson, 0, dataHandler);
            ExcelUtils.downloadExcelFile(response, request, excelResidentTitle, byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            setCookieError(request, response);
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术加载社区居民列表
     *
     * @param session     Session对象
     * @param page        分页页码
     * @param companyId   单位编号
     * @param companyType 单位类别编号
     * @param name        社区居民姓名
     * @param address     社区居民家庭地址
     * @param phone       社区居民联系方式
     * @param isSearch    是否搜索
     * @return Ajax消息
     */
    @PostMapping("/ajax_list")
    @ResponseBody
    public Map<String, Object> findCommunityResidentsForAjax(HttpSession session, Integer page, Long companyId, Long companyType, String name, String address, String phone, Boolean isSearch) {
        getSessionRoleId(session);
        Map<String, Object> communityResidentMap;
        try {
            if (isSearch != null && isSearch) {
                CommunityResident communityResident = new CommunityResident();
                communityResident.setName(name);
                communityResident.setAddress(address);
                communityResident.setPhones(phone);
                communityResidentMap = communityResidentService.find(systemUser, systemAdministratorId, communityCompanyType, subdistrictCompanyType, communityResident, companyId, companyType, page, null);
            } else {
                communityResidentMap = communityResidentService.findCorrelation(systemUser, systemCompanyType, communityCompanyType, subdistrictCompanyType, page, null);
            }
            return setJsonMap(communityResidentMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("查找社区居民失败！", e);
        }
    }
}
