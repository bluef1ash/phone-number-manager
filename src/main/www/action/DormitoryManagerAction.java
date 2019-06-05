package www.action;

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
import utils.ExcelUtil;
import www.entity.Community;
import www.entity.DormitoryManager;
import www.service.CommunityService;
import www.service.DormitoryManagerService;
import www.validator.DormitoryManagerInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区楼长控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/dormitory")
public class DormitoryManagerAction extends BaseAction {
    private final HttpServletRequest request;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private CommunityService communityService;

    @Autowired
    public DormitoryManagerAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("dormitoryManagerCreateOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new DormitoryManagerInputValidator(dormitoryManagerService, request));
        }
    }

    /**
     * 社区楼长列表
     *
     * @param session Session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String dormitoryManagerList(HttpSession session, Model model) {
        setPersonVariable(session, model);
        try {
            Map<String, Object> dormitoryManagerMap = dormitoryManagerService.findDormitoryManagersAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, null, null);
            model.addAttribute("dormitoryManagers", dormitoryManagerMap.get("data"));
            model.addAttribute("pageInfo", dormitoryManagerMap.get("pageInfo"));
            model.addAttribute("dataType", 1);
            return "dormitory/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加社区楼长
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createDormitoryManager(HttpSession session, Model model) {
        getSessionRoleId(session);
        try {
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser, communityRoleId, subdistrictRoleId);
            model.addAttribute("communities", JSON.toJSON(communities));
            return "dormitory/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 编辑社区楼长
     *
     * @param session session对象
     * @param model   前台模型
     * @param id      需要编辑的社区楼长的编号
     * @return 视图页面
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editDormitoryManager(HttpSession session, Model model, String id) {
        getSessionRoleId(session);
        try {
            DormitoryManager dormitoryManager = dormitoryManagerService.findDormitoryManagerAndCommunityById(id);
            model.addAttribute("dormitoryManager", dormitoryManager);
            List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser, communityRoleId, subdistrictRoleId);
            model.addAttribute("communities", communities);
            return "dormitory/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改社区楼长处理
     *
     * @param httpServletRequest HTTP请求对象
     * @param session            Session对象
     * @param model              前台模型
     * @param dormitoryManager   前台传递的社区楼长对象
     * @param bindingResult      错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String dormitoryManagerCreateOrEditHandle(HttpServletRequest httpServletRequest, HttpSession session, Model model, @Validated DormitoryManager dormitoryManager, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            try {
                throwsError(communityService, model, bindingResult);
                return "dormitory/edit";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("系统出现错误，请联系管理员！");
            }
        }
        if (RequestMethod.POST.toString().equals(httpServletRequest.getMethod())) {
            try {
                dormitoryManagerService.createDormitoryManager(dormitoryManager);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加社区楼长失败！", e);
            }
        } else {
            try {
                dormitoryManagerService.updateDormitoryManager(dormitoryManager);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改社区楼长失败！", e);
            }
        }
        return "redirect:/dormitory/list";
    }

    /**
     * 使用AJAX技术通过社区楼长编号删除
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @RequestMapping(value = "/ajax_delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteDormitoryManagerForAjax(String id) {
        try {
            Map<String, Object> jsonMap = new HashMap<>(3);
            dormitoryManagerService.deleteObjectById(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区楼长成功！");
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除社区楼长失败！", e);
        }
    }

    /**
     * 导入楼长信息进系统
     *
     * @param session       session对象
     * @param request       HTTP请求对象
     * @param subdistrictId 导入的街道编号
     * @return Ajax信息
     */
    @RequestMapping(value = "/import_as_system", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> dormitoryManagerImportAsSystem(HttpSession session, HttpServletRequest request, Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        try {
            Workbook workbook = uploadExcel(request, session, "excel_dormitory_title");
            jsonMap.put("state", dormitoryManagerService.addDormitoryManagerFromExcel(workbook, subdistrictId, configurationsMap));
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("上传文件失败！", e);
        }
    }

    /**
     * 导出社区楼长信息到Excel
     *
     * @param session  session对象
     * @param response 前台响应对象
     * @param data     传递数据
     */
    @RequestMapping(value = "/save_as_excel", method = RequestMethod.GET)
    public void dormitoryManagerSaveAsExcel(HttpSession session, HttpServletResponse response, String data) {
        List<Map<String, Object>> userData = getUserData(session, data);
        //获取属性-列头
        Map<String, String> headMap = dormitoryManagerService.getPartStatHead();
        String excelDormitoryTitleUp = CommonUtil.convertConfigurationString(configurationsMap.get("excel_dormitory_title_up"));
        String excelDormitoryTitle = CommonUtil.convertConfigurationString(configurationsMap.get("excel_dormitory_title"));
        try {
            // 获取业务数据集
            JSONArray dataJson = dormitoryManagerService.findDormitoryManagersAndCommunitiesBySystemUserId(configurationsMap, userData, new String[]{excelDormitoryTitleUp, excelDormitoryTitle});
            Map<String, Object> dataHandler = dormitoryManagerService.setExcelHead();
            ByteArrayOutputStream byteArrayOutputStream = ExcelUtil.exportExcelX(headMap, dataJson, 0, (ExcelUtil.DataHandler) dataHandler.get("security/handler"));
            ExcelUtil.downloadExcelFile(response, request, (String) dataHandler.get("fileName"), byteArrayOutputStream);
        } catch (Exception e) {
            setCookieError(request, response);
            e.printStackTrace();
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术加载社区楼长列表
     *
     * @param session       Session对象
     * @param page          分页页码
     * @param isSearch      是否搜索
     * @param companyId     单位编号
     * @param companyRoleId 单位类别编号
     * @param name          社区楼长姓名
     * @param sex           社区楼长性别
     * @param address       社区楼长家庭地址
     * @param phone         社区楼长联系方式
     * @return Ajax消息
     */
    @RequestMapping(value = "/ajax_list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> findDormitoryManagersForAjax(HttpSession session, Integer page, Boolean isSearch, Long companyId, Long companyRoleId, String name, Integer sex, String address, String phone) {
        getSessionRoleId(session);
        Map<String, Object> dormitoryManagerMap;
        try {
            if (isSearch != null && isSearch) {
                DormitoryManager dormitoryManager = new DormitoryManager();
                dormitoryManager.setName(name);
                dormitoryManager.setSex(sex);
                dormitoryManager.setAddress(address);
                dormitoryManager.setTelephones(phone);
                dormitoryManagerMap = dormitoryManagerService.findDormitoryManagerByDormitoryManager(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, dormitoryManager, companyId, companyRoleId, page, null);
            } else {
                dormitoryManagerMap = dormitoryManagerService.findDormitoryManagersAndCommunity(systemUser, systemRoleId, communityRoleId, subdistrictRoleId, page, null);
            }
            return setJsonMap(dormitoryManagerMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("查找社区楼长失败！", e);
        }
    }

    /**
     * 通过社区编号加载最后一个编号
     *
     * @param communityId     社区编号
     * @param communityName   社区名称
     * @param subdistrictName 街道办事处名称
     * @return JSON数据
     */
    @RequestMapping(value = "/ajax_id", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> loadDormitoryManagerLastIdForAjax(Long communityId, String communityName, String subdistrictName) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            String lastId = dormitoryManagerService.findLastId(communityId, communityName, subdistrictName);
            jsonMap.put("state", 1);
            jsonMap.put("id", lastId);
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("加载编号失败！", e);
        }
    }
}
