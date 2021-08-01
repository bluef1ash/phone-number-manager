package com.github.phonenumbermanager.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.PhoneTypeEnum;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.RedisUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.core.context.SecurityContextHolder;
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
        systemCompanyType = Convert.toInt(configurationsMap.get("system_company_type"));
        communityCompanyType = Convert.toInt(configurationsMap.get("community_company_type"));
        subdistrictCompanyType = Convert.toInt(configurationsMap.get("subdistrict_company_type"));
        systemAdministratorId = Convert.toLong(configurationsMap.get("system_administrator_id"));
    }

    /**
     * 上传Excel
     *
     * @param request        HTTP请求对象
     * @param startRowNumber 开始读取的行数
     * @return Excel工作簿对象
     * @throws IOException IO异常
     */
    List<List<Object>> uploadExcel(HttpServletRequest request, int startRowNumber) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException("文件不存在！");
        }
        InputStream inputStream = file.getInputStream();
        ExcelReader excelReader = ExcelUtil.getReader(inputStream, 0);
        excelReader.setIgnoreEmptyRow(true);
        return excelReader.read(startRowNumber);
    }

    /**
     * 获取解密后数据
     *
     * @param encodeData 传递的加密数据
     * @return 解码后用户数据
     * @throws ClassCastException 转换异常
     */
    @SuppressWarnings("all")
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
                if (PhoneUtil.isTel(phoneNumber.getPhoneNumber())) {
                    phoneNumber.setPhoneType(PhoneTypeEnum.LANDLINE);
                } else if (PhoneUtil.isMobile(phoneNumber.getPhoneNumber())) {
                    phoneNumber.setPhoneType(PhoneTypeEnum.MOBILE);
                } else {
                    phoneNumber.setPhoneType(PhoneTypeEnum.UNKNOWN);
                }
            }
        }
    }

    /**
     * 设置单元格样式
     *
     * @param cellStyle   单元格对象
     * @param excelWriter Excel写入器
     * @param fontName    字体名称
     * @param fontHeight  字体大小
     * @param isBold      是否加粗
     * @param isBorder    是否有边框
     * @param isWrapText  是否自动换行
     */
    public void setCellStyle(CellStyle cellStyle, ExcelWriter excelWriter, String fontName, short fontHeight, boolean isBold, boolean isBorder, boolean isWrapText) {
        cellStyle.setFillPattern(FillPatternType.NO_FILL);
        if (isBorder) {
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
        }
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = excelWriter.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontHeight);
        font.setBold(isBold);
        cellStyle.setFont(font);
        cellStyle.setWrapText(isWrapText);
    }
}
