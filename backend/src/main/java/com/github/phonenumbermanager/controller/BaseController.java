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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
    protected SystemUser systemUser;
    protected Map<String, JSONObject> configurationMap;
    protected JSONObject search;
    protected JSONObject sort;

    /**
     * 获取环境变量
     */
    protected void getEnvironmentVariable() {
        if (!SystemConstant.ANONYMOUS_USER
            .equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (configurationMap == null || configurationMap.isEmpty()) {
            configurationMap =
                JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY)).toBean(Map.class);
        }
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

    /**
     *
     * @param request
     *            HTTP请求对象
     */
    protected void getSearchParameter(HttpServletRequest request) {
        String params = request.getParameter("params");
        if (StrUtil.isEmptyIfStr(params)) {
            search = null;
            sort = null;
        } else {
            JSONObject paramsJson = JSONUtil.parseObj(params);
            search = (JSONObject)paramsJson.get("search");
            sort = (JSONObject)paramsJson.get("sort");
        }
    }

    /**
     * 获取查找Wrapper
     *
     * @param request
     *            HTTP请求对象
     * @param wrapper
     *            查询条件对象
     */
    protected void getSearchWrapper(HttpServletRequest request, QueryWrapper<?> wrapper) {
        getSearchParameter(request);
        if (search != null) {
            for (Map.Entry<String, Object> entry : search.entrySet()) {
                wrapper.and(w -> w.like(StrUtil.toUnderlineCase(entry.getKey()), entry.getValue()));
            }
        }
        if (sort != null) {
            for (Map.Entry<String, Object> entry : sort.entrySet()) {
                wrapper.orderBy(true, "ascend".equals(entry.getValue()),
                    "CONVERT(" + StrUtil.toUnderlineCase(entry.getKey()) + " USING gbk) COLLATE gbk_chinese_ci");
            }
        }
    }
}
