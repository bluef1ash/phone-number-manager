package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.vo.ComputedVo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

/**
 * 控制器父类
 *
 * @author 廿二月的天
 */
@CrossOrigin
abstract class BaseController {
    protected JSONObject search;
    protected JSONObject sort;

    /**
     * 上传Excel
     *
     * @param request
     *            HTTP请求对象
     * @param startRowNumber
     *            开始读取的行数
     * @return Excel工作簿对象
     */
    protected List<List<Object>> uploadExcelFileToData(HttpServletRequest request, int startRowNumber) {
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

    /**
     * 下载Excel文件
     *
     * @param response
     *            HTTP响应对象
     * @param excelWriter
     *            Excel写入对象
     * @param filename
     *            文件名
     */
    protected void downloadExcelFile(HttpServletResponse response, ExcelWriter excelWriter, String filename) {
        if (excelWriter != null) {
            response.setCharacterEncoding("utf-8");
            response.setContentType(excelWriter.getContentType());
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition",
                excelWriter.getDisposition(filename + System.currentTimeMillis(), null));
            try (excelWriter) {
                ServletOutputStream outputStream = response.getOutputStream();
                excelWriter.flush(outputStream, true);
            } catch (IOException e) {
                throw new BusinessException("导出Excel文件失败！", e);
            }
        }
    }

    /**
     * 获取单位编号数组
     *
     * @param computedVo
     *            计算视图对象
     * @return 单位编号数组
     */
    protected Long[] getCompanyIds(ComputedVo computedVo) {
        Long[] companyIds = null;
        if (computedVo != null && computedVo.getCompanyIds() != null) {
            companyIds = computedVo.getCompanyIds();
        }
        return companyIds;
    }

    /**
     * 获取系统用户所属单位
     *
     * @param configurationMap
     *            系统配置集合
     * @param companyService
     *            单位业务层模块
     * @return 系统用户所属单位
     */
    protected List<Company> getUserCompanies(Map<String, JSONObject> configurationMap, SystemUser systemUser,
        CompanyService companyService) {
        List<Company> companies = systemUser.getCompanies();
        if (companies == null && systemUser.getId()
            .equals(Convert.toLong(configurationMap.get("system_administrator_id").get("content")))) {
            companies = companyService.list(new LambdaQueryWrapper<Company>().eq(Company::getParentId, 0));
        }
        return companies;
    }
}
