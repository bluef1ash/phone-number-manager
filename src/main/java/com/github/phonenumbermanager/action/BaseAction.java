package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.ExcelUtil;
import com.github.phonenumbermanager.util.StringCheckedRegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器父类
 *
 * @author 廿二月的天
 */
abstract class BaseAction {
    Integer systemCompanyType;
    Integer communityCompanyType;
    Integer subdistrictCompanyType;
    SystemUser systemUser;
    Map<String, Object> configurationsMap;
    Long systemAdministratorId;

    /**
     * 获取Session内的角色编号
     *
     * @param session Session对象
     */
    void getSessionRoleId(HttpSession session) {
        systemUser = (SystemUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        systemCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_company_type"));
        communityCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("community_company_type"));
        subdistrictCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("subdistrict_company_type"));
        systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
    }

    /**
     * 设置导入页面Cookie
     *
     * @param session Session对象
     * @param model   前台模型
     */
    void setPersonVariable(HttpSession session, Model model) {
        getSessionRoleId(session);
        Map<String, Integer> companyTypes = new HashMap<>(4);
        companyTypes.put("systemCompanyType", systemCompanyType);
        companyTypes.put("communityCompanyType", communityCompanyType);
        companyTypes.put("subdistrictCompanyType", subdistrictCompanyType);
        model.addAttribute("companyTypes", companyTypes);
        model.addAttribute("companyType", systemUser.getLevel());
    }

    /**
     * 居民与楼长保存或更新时出现错误
     *
     * @param communityService 社区Service对象
     * @param model            前台模型
     * @param bindingResult    错误信息对象
     */
    void throwsError(CommunityService communityService, Model model, BindingResult bindingResult) {
        List<Community> communities = communityService.get(systemUser, communityCompanyType, subdistrictCompanyType);
        model.addAttribute("communities", communities);
        // 输出错误信息
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        model.addAttribute("messageErrors", allErrors);
    }

    /**
     * 上传Excel
     *
     * @param request    HTTP请求对象
     * @param session    Session对象
     * @param excelTitle Excel文件标题字段
     * @return Excel工作簿对象
     * @throws IOException SERVICE层异常
     */
    Workbook uploadExcel(HttpServletRequest request, HttpSession session, String excelTitle) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException("文件不存在！");
        }
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            getSessionRoleId(session);
            filename = CommonUtil.convertConfigurationString(configurationsMap.get(excelTitle));
        }
        InputStream inputStream = file.getInputStream();
        return ExcelUtil.getWorkbook(inputStream, filename);
    }

    /**
     * 获取解密后数据
     *
     * @param session    Session对象
     * @param encodeData 传递的加密数据
     * @return 解码后用户数据
     * @throws ClassCastException 转换异常
     */
    List<Map<String, Object>> getDecodeData(HttpSession session, String encodeData) throws ClassCastException {
        getSessionRoleId(session);
        byte[] jsonDecode = Base64.getDecoder().decode(encodeData);
        return (List<Map<String, Object>>) JSON.parse(jsonDecode);
    }

    /**
     * 设置返回JSON数据
     *
     * @param data 数据对象集合
     * @return JSON数据
     */
    Map<String, Object> setJsonMap(IPage<?> data) {
        Map<String, Object> jsonMap = new HashMap<>(4);
        jsonMap.put("state", 1);
        jsonMap.put("data", data);
        return jsonMap;
    }

    /**
     * 设置错误Cookie
     *
     * @param request  HTTP请求对象
     * @param response HTTP相应对象
     */
    void setCookieError(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("exportExcel".equals(cookie.getName())) {
                cookie.setValue("error");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }


    /**
     * 设置联系方式集合
     *
     * @param phoneNumbers              需要设置的集合
     * @param phoneNumberSourceTypeEnum 来源类型
     * @param sourceId                  来源编号
     */
    void setPhoneNumbers(List<PhoneNumber> phoneNumbers, PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum, String sourceId) {
        for (PhoneNumber phoneNumber : phoneNumbers) {
            phoneNumber.setSourceType(phoneNumberSourceTypeEnum);
            phoneNumber.setSourceId(sourceId);
            if (phoneNumber.getPhoneType() == null) {
                switch (StringCheckedRegexUtil.checkPhone(phoneNumber.getPhoneNumber())) {
                    case LANDLINE:
                        phoneNumber.setPhoneType(PhoneTypeEnum.LANDLINE);
                        break;
                    case MOBILE:
                        phoneNumber.setPhoneType(PhoneTypeEnum.MOBILE);
                        break;
                    default:
                        phoneNumber.setPhoneType(PhoneTypeEnum.UNKNOWN);
                        break;
                }
            }
        }
    }
}
