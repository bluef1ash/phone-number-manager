package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.GenderEnum;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.ExcelUtil;
import com.github.phonenumbermanager.validator.DormitoryManagerInputValidator;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.format.datetime.DateFormatter;
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
import java.util.ArrayList;
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
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private CommunityService communityService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod())) {
            binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
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
    @GetMapping
    public String dormitoryManagerList(HttpSession session, Model model) {
        setPersonVariable(session, model);
        model.addAttribute("dormitoryManagers", dormitoryManagerService.getCorrelation(systemUser, PhoneNumberSourceTypeEnum.DORMITORY_MANAGER, communityCompanyType, subdistrictCompanyType, null, null));
        model.addAttribute("dataType", 1);
        return "dormitory/list";
    }

    /**
     * 添加社区楼长
     *
     * @param session session对象
     * @param model   前台模型
     * @return 视图页面
     */
    @GetMapping("/create")
    public String createDormitoryManager(HttpSession session, Model model) {
        getSessionRoleId(session);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", JSON.toJSON(communities));
        return "dormitory/edit";
    }

    /**
     * 编辑社区楼长
     *
     * @param session session对象
     * @param model   前台模型
     * @param id      需要编辑的社区楼长的编号
     * @return 视图页面
     */
    @GetMapping("/edit/{id}")
    public String editDormitoryManager(HttpSession session, Model model, @PathVariable String id) {
        getSessionRoleId(session);
        DormitoryManager dormitoryManager = dormitoryManagerService.getCorrelation(id);
        model.addAttribute("dormitoryManager", dormitoryManager);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", communities);
        return "dormitory/edit";
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
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public String dormitoryManagerCreateOrEditHandle(HttpServletRequest httpServletRequest, HttpSession session, Model model, @Validated DormitoryManager dormitoryManager, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            throwsError(communityService, model, bindingResult);
            return "dormitory/edit";
        }
        if (RequestMethod.POST.toString().equals(httpServletRequest.getMethod())) {
            if (communityService.isSubmittedById(1, dormitoryManager.getCommunityId())) {
                throw new BusinessException("此社区已经提报，不允许添加！");
            }
            if (dormitoryManagerService.save(dormitoryManager)) {
                setPhoneNumbers(dormitoryManager.getPhoneNumbers(), PhoneNumberSourceTypeEnum.DORMITORY_MANAGER, dormitoryManager.getId());
                phoneNumberService.saveBatch(dormitoryManager.getPhoneNumbers());
            } else {
                throw new BusinessException("添加社区楼长失败！");
            }
        } else {
            if (dormitoryManagerService.updateById(dormitoryManager)) {
                setPhoneNumbers(dormitoryManager.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY, dormitoryManager.getId());
                phoneNumberService.saveOrUpdateBatch(dormitoryManager.getPhoneNumbers());
            } else {
                throw new BusinessException("修改社区楼长失败！");
            }
        }
        return "redirect:/dormitory";
    }

    /**
     * 使用AJAX技术通过社区楼长编号删除
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, Object> deleteDormitoryManagerForAjax(@PathVariable String id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (dormitoryManagerService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区楼长成功！");
            return jsonMap;
        }
        throw new JsonException("删除社区楼长失败！");
    }

    /**
     * 导入楼长信息进系统
     *
     * @param session       session对象
     * @param request       HTTP请求对象
     * @param subdistrictId 导入的街道编号
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ResponseBody
    public Map<String, Object> dormitoryManagerImportAsSystem(HttpSession session, HttpServletRequest request, @RequestParam Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            Workbook workbook = uploadExcel(request, session, "excel_dormitory_title");
            dormitoryManagerService.save(workbook, subdistrictId, configurationsMap);
            jsonMap.put("state", 1);
            jsonMap.put("message", "上传成功！");
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
    @GetMapping("/download")
    public void dormitoryManagerSaveAsExcel(HttpSession session, HttpServletResponse response, @RequestParam String data) {
        List<Map<String, Object>> userData = getDecodeData(session, data);
        // 获取属性-列头
        Map<String, String> headMap = dormitoryManagerService.getPartStatHead();
        String excelDormitoryTitleUp = CommonUtil.convertConfigurationString(configurationsMap.get("excel_dormitory_title_up"));
        String excelDormitoryTitle = CommonUtil.convertConfigurationString(configurationsMap.get("excel_dormitory_title"));
        ExcelUtil.DataHandler excelDataHandler = dormitoryManagerService.getExcelDataHandler();
        // 获取业务数据集
        JSONArray dataJson = dormitoryManagerService.getCorrelation(communityCompanyType, subdistrictCompanyType, userData, new String[]{excelDormitoryTitleUp, excelDormitoryTitle});
        try {
            ByteArrayOutputStream byteArrayOutputStream = ExcelUtil.exportExcelX(excelDormitoryTitle, headMap, dataJson, 0, excelDataHandler);
            ExcelUtil.downloadExcelFile(response, request, dormitoryManagerService.getFileTitle(), byteArrayOutputStream);
        } catch (Exception e) {
            setCookieError(request, response);
            e.printStackTrace();
            throw new BusinessException("导出Excel文件失败！");
        }
    }

    /**
     * 使用AJAX技术加载社区楼长列表
     *
     * @param session     Session对象
     * @param page        分页页码
     * @param isSearch    是否搜索
     * @param companyId   单位编号
     * @param companyType 单位类别编号
     * @param name        社区楼长姓名
     * @param gender      社区楼长性别
     * @param address     社区楼长家庭地址
     * @param phone       社区楼长联系方式
     * @return Ajax消息
     */
    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> findDormitoryManagersForAjax(HttpSession session, Integer page, Boolean isSearch, Long companyId, Integer companyType, String name, GenderEnum gender, String address, String phone) {
        getSessionRoleId(session);
        IPage<DormitoryManager> dormitoryManagerMap;
        if (isSearch != null && isSearch) {
            DormitoryManager dormitoryManager = new DormitoryManager();
            dormitoryManager.setName(name);
            dormitoryManager.setGender(gender);
            dormitoryManager.setAddress(address);
            List<String> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(phone);
            dormitoryManager.setPhoneNumbers(CommonUtil.setPhoneNumbers(phoneNumbers));
            dormitoryManagerMap = dormitoryManagerService.get(systemUser, systemCompanyType, communityCompanyType, subdistrictCompanyType, dormitoryManager, companyId, companyType, page, null);
        } else {
            dormitoryManagerMap = dormitoryManagerService.getCorrelation(systemUser, PhoneNumberSourceTypeEnum.DORMITORY_MANAGER, communityCompanyType, subdistrictCompanyType, page, null);
        }
        return setJsonMap(dormitoryManagerMap);
    }

    /**
     * 通过社区编号加载最后一个编号
     *
     * @param communityId     社区编号
     * @param communityName   社区名称
     * @param subdistrictName 街道办事处名称
     * @return JSON数据
     */
    @PostMapping("/last_id")
    @ResponseBody
    public Map<String, Object> loadDormitoryManagerLastIdForAjax(@RequestParam Long communityId, String communityName, String subdistrictName) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        String lastId = dormitoryManagerService.get(communityId, communityName, subdistrictName);
        jsonMap.put("state", 1);
        jsonMap.put("id", lastId);
        return jsonMap;
    }
}
