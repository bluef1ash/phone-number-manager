package com.github.phonenumbermanager.controller;

import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.PhoneTypeEnum;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.ExcelUtil;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.util.StringCheckedRegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 控制器父类
 *
 * @author 廿二月的天
 */
@CrossOrigin
abstract class BaseController {
    Integer systemCompanyType;
    Integer communityCompanyType;
    Integer subdistrictCompanyType;
    SystemUser systemUser;
    Map<String, Object> configurationsMap;
    Long systemAdministratorId;
    @Resource
    protected RedisUtil redisUtil;

    /**
     * 获取角色编号
     */
    @SuppressWarnings("all")
    void getRoleId() {
        systemUser = (SystemUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        configurationsMap = (Map<String, Object>) redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        systemCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_company_type"));
        communityCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("community_company_type"));
        subdistrictCompanyType = CommonUtil.convertConfigurationInteger(configurationsMap.get("subdistrict_company_type"));
        systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
    }

    /**
     * 设置导入页面Cookie
     *
     * @param jsonMap JSON对象
     */
    void setPersonVariable(Map<String, Object> jsonMap) {
        getRoleId();
        jsonMap.put("systemCompanyType", systemCompanyType);
        jsonMap.put("communityCompanyType", communityCompanyType);
        jsonMap.put("subdistrictCompanyType", subdistrictCompanyType);
        jsonMap.put("companyType", systemUser.getLevel());
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
     * @param excelTitle Excel文件标题字段
     * @return Excel工作簿对象
     * @throws IOException SERVICE层异常
     */
    Workbook uploadExcel(HttpServletRequest request, String excelTitle) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException("文件不存在！");
        }
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            getRoleId();
            filename = CommonUtil.convertConfigurationString(configurationsMap.get(excelTitle));
        }
        InputStream inputStream = file.getInputStream();
        return ExcelUtil.getWorkbook(inputStream, filename);
    }

    /**
     * 获取解密后数据
     *
     * @param encodeData 传递的加密数据
     * @return 解码后用户数据
     * @throws ClassCastException 转换异常
     */
    List<Map<String, Object>> getDecodeData(String encodeData) throws ClassCastException {
        getRoleId();
        byte[] jsonDecode = Base64.getDecoder().decode(encodeData);
        return (List<Map<String, Object>>) JSON.parse(jsonDecode);
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
