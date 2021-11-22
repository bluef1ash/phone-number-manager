package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.style.StyleUtil;

/**
 * 控制器父类
 *
 * @author 廿二月的天
 */
@CrossOrigin
abstract class BaseController {
    @Resource
    private RedisUtil redisUtil;
    SystemUser systemUser;
    Map<String, Configuration> configurationsMap;
    Long systemAdministratorId;

    /**
     * 获取环境变量
     */
    @SuppressWarnings("all")
    protected void getEnvironmentVariable() {
        systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        configurationsMap = (Map<String, Configuration>)redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        systemAdministratorId = Convert.toLong(configurationsMap.get("system_administrator_id").getContent());
    }

    /**
     * 上传Excel
     *
     * @param request
     *            HTTP请求对象
     * @param startRowNumber
     *            开始读取的行数
     * @return Excel工作簿对象
     */
    protected List<List<Object>> uploadExcel(HttpServletRequest request, int startRowNumber) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            return null;
        }
        try (InputStream inputStream = file.getInputStream()) {
            ExcelReader excelReader = ExcelUtil.getReader(inputStream, 0);
            excelReader.setIgnoreEmptyRow(true);
            return excelReader.read(startRowNumber);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 设置单元格样式
     *
     * @param cellStyle
     *            单元格对象
     * @param excelWriter
     *            Excel写入器
     * @param fontName
     *            字体名称
     * @param fontHeight
     *            字体大小
     * @param isBold
     *            是否加粗
     * @param isBorder
     *            是否有边框
     * @param isWrapText
     *            是否自动换行
     */
    protected void setCellStyle(CellStyle cellStyle, ExcelWriter excelWriter, String fontName, short fontHeight,
        boolean isBold, boolean isBorder, boolean isWrapText) {
        StyleUtil.setColor(cellStyle, IndexedColors.AUTOMATIC, FillPatternType.NO_FILL);
        if (isBorder) {
            StyleUtil.setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
        }
        StyleUtil.setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        Font font = excelWriter.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontHeight);
        font.setBold(isBold);
        cellStyle.setFont(font);
        cellStyle.setWrapText(isWrapText);
    }
}
