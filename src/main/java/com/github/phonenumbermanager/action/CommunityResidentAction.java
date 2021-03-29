package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.ExcelUtil;
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
import java.io.IOException;
import java.util.ArrayList;
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
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod())) {
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
    @GetMapping
    public String communityResidentList(HttpSession session, Model model) {
        setPersonVariable(session, model);
        model.addAttribute("communityResidents", communityResidentService.getCorrelation(systemUser, PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT, communityCompanyType, subdistrictCompanyType, null, null));
        model.addAttribute("dataType", 0);
        return "resident/list";
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
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", JSON.toJSON(communities));
        return "resident/edit";
    }

    /**
     * 编辑社区居民
     *
     * @param session session对象
     * @param model   前台模型
     * @param id      需要编辑的社区居民的编号
     * @return 视图页面
     */
    @GetMapping("/edit/{id}")
    public String editCommunityResident(HttpSession session, Model model, @PathVariable Long id) {
        getSessionRoleId(session);
        CommunityResident communityResident = communityResidentService.getCorrelation(id);
        model.addAttribute("communityResident", communityResident);
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", communities);
        return "resident/edit";
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
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public String communityResidentCreateOrEditHandle(HttpServletRequest httpServletRequest, HttpSession session, Model model, @Validated CommunityResident communityResident, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            getSessionRoleId(session);
            throwsError(communityService, model, bindingResult);
            return "resident/edit";
        }
        if (RequestMethod.POST.toString().equals(httpServletRequest.getMethod())) {
            if (communityService.isSubmittedById(0, communityResident.getCommunityId())) {
                throw new BusinessException("此社区已经提报，不允许添加！");
            }
            multiplePhoneHandler(communityResident);
            if (communityResidentService.save(communityResident)) {
                setPhoneNumbers(communityResident.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT, String.valueOf(communityResident.getId()));
                phoneNumberService.saveBatch(communityResident.getPhoneNumbers());
            } else {
                throw new BusinessException("添加社区居民失败！");
            }
        } else {
            if (communityResidentService.updateById(communityResident)) {
                setPhoneNumbers(communityResident.getPhoneNumbers(), PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT, String.valueOf(communityResident.getId()));
                phoneNumberService.saveOrUpdateBatch(communityResident.getPhoneNumbers());
            } else {
                throw new BusinessException("修改社区居民失败！");
            }
        }
        return "redirect:/resident";
    }

    /**
     * 使用AJAX技术通过社区居民编号删除社区居民
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, Object> deleteCommunityResidentForAjax(@PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (communityResidentService.removeById(id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除社区居民成功！");
            return jsonMap;
        }
        throw new JsonException("删除社区居民失败！");
    }

    /**
     * 导入居民信息进系统
     *
     * @param request       HTTP请求对象
     * @param session       session对象
     * @param subdistrictId 导入的街道编号
     * @return Ajax信息
     */
    @PostMapping("/import")
    @ResponseBody
    public Map<String, Object> communityResidentImportAsSystem(HttpSession session, HttpServletRequest request, Long subdistrictId) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            Workbook workbook = uploadExcel(request, session, "excel_resident_title");
            jsonMap.put("state", 1);
            jsonMap.put("create", communityResidentService.save(workbook, subdistrictId, configurationsMap));
            return jsonMap;
        } catch (IOException e) {
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
    @GetMapping("/download")
    public void communityResidentSaveAsExcel(HttpSession session, HttpServletResponse response, String data) {
        List<Map<String, Object>> userData = getDecodeData(session, data);
        // 获取属性-列头
        Map<String, String> headMap = communityResidentService.getPartStatHead();
        String excelResidentTitleUp = CommonUtil.convertConfigurationString(configurationsMap.get("excel_resident_title_up"));
        String excelResidentTitle = CommonUtil.convertConfigurationString(configurationsMap.get("excel_resident_title"));
        ExcelUtil.DataHandler dataHandler = communityResidentService.setExcelHead(new String[]{excelResidentTitleUp, excelResidentTitle});
        // 获取业务数据集
        JSONArray dataJson = communityResidentService.getCorrelation(communityCompanyType, subdistrictCompanyType, userData);
        try {
            ByteArrayOutputStream byteArrayOutputStream = ExcelUtil.exportExcelX(excelResidentTitle, headMap, dataJson, 0, dataHandler);
            ExcelUtil.downloadExcelFile(response, request, excelResidentTitle, byteArrayOutputStream);
        } catch (IOException e) {
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
    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> findCommunityResidentsForAjax(HttpSession session, Integer page, Long companyId, Long companyType, String name, String address, String phone, Boolean isSearch) {
        getSessionRoleId(session);
        IPage<CommunityResident> communityResidentMap;
        if (isSearch != null && isSearch) {
            CommunityResident communityResident = new CommunityResident();
            communityResident.setName(name);
            communityResident.setAddress(address);
            List<String> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(phone);
            communityResident.setPhoneNumbers(CommonUtil.setPhoneNumbers(phoneNumbers));
            communityResidentMap = communityResidentService.get(systemUser, systemAdministratorId, communityCompanyType, subdistrictCompanyType, communityResident, companyId, companyType, page, null);
        } else {
            communityResidentMap = communityResidentService.getCorrelation(systemUser, PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT, communityCompanyType, subdistrictCompanyType, page, null);
        }
        return setJsonMap(communityResidentMap);
    }


    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident 需要处理的社区居民对象
     */
    private void multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setName(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident.setAddress(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getAddress())).replaceAll("—", "-"));
    }
}
