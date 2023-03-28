package com.github.phonenumbermanager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.ImportOrExportStatusEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.util.R;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.vo.ComputedVO;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
     * 上传文件处理
     *
     * @param request
     *            HTTP 请求对象
     * @param redisUtil
     *            Redis 工具类
     * @param importId
     *            导入编号
     * @return 输入流
     */
    protected InputStream uploadToData(HttpServletRequest request, RedisUtil redisUtil, Long importId)
        throws IOException {
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.UPLOAD.getValue(), 20, TimeUnit.MINUTES);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件失败！");
        }
        return file.getInputStream();
    }

    /**
     * 获取搜索参数
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
     * 获取搜索 Wrapper
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
     * 下载 Excel 文件
     *
     * @param response
     *            HTTP 响应对象
     * @param redisUtil
     *            Redis 工具类
     * @param exportId
     *            导出编号
     * @param fileName
     *            文件名
     */
    protected void downloadExcelFile(HttpServletResponse response, RedisUtil redisUtil, Long exportId,
        String fileName) {
        fileName = StrUtil.format("attachment; filename=\"{}\"", StrUtil.addSuffixIfNot(
            URLUtil.encodeAll(fileName + System.currentTimeMillis(), CharsetUtil.CHARSET_UTF_8), ".xlsx"));
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(ExcelUtil.XLSX_CONTENT_TYPE);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", fileName);
        String excelFilePath = FileUtil.getTmpDirPath() + SystemConstant.EXPORT_ID_KEY + exportId + ".xlsx";
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] bytes = FileUtil.readBytes(excelFilePath);
            FileUtil.del(excelFilePath);
            outputStream.write(bytes);
            redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId,
                ImportOrExportStatusEnum.DONE.getValue(), 20, TimeUnit.MINUTES);
        } catch (IOException e) {
            redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId,
                ImportOrExportStatusEnum.FAILED.getValue(), 20, TimeUnit.MINUTES);
            throw new BusinessException("下载 Excel 文件失败！", e);
        }
    }

    /**
     * 获取单位编号数组
     *
     * @param computedVo
     *            计算视图对象
     * @return 单位编号数组
     */
    protected Long[] getCompanyIds(ComputedVO computedVo) {
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

    /**
     * 导入数据进系统
     *
     * @param request
     *            HTTP 请求对象
     * @param importId
     *            导入编号
     * @param configurationService
     *            配置业务
     * @param service
     *            社区居民或者楼片长业务类
     * @param redisUtil
     *            缓存工具类
     * @param readExcelStartRowNumber
     *            Excel 数据开始的行数
     * @return 前台输出
     */
    protected R importForSystem(HttpServletRequest request, Long importId, ConfigurationService configurationService,
        BaseService<?> service, RedisUtil redisUtil, String readExcelStartRowNumber) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        if (importId == null) {
            Map<String, JSONObject> configurationMap = configurationService.mapAll();
            int startRowNumber = Convert.toInt(configurationMap.get(readExcelStartRowNumber).get("content"));
            importId = IdWorker.getId();
            redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
                ImportOrExportStatusEnum.START.getValue(), 20, TimeUnit.MINUTES);
            try {
                InputStream inputStream = uploadToData(request, redisUtil, importId);
                service.asyncImport(inputStream, startRowNumber, importId);
            } catch (IOException e) {
                redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
                    ImportOrExportStatusEnum.FAILED.getValue(), 20, TimeUnit.MINUTES);
                throw new JsonException("上传文件失败！", e);
            }
        }
        jsonMap.put("status", redisUtil.get(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId));
        jsonMap.put("importId", importId.toString());
        return R.ok(jsonMap);
    }

    /**
     * 导出 Excel 文件
     *
     * @param exportId
     *            导出编号
     * @param configurationService
     *            配置业务
     * @param service
     *            社区居民或者楼片长业务类
     * @param redisUtil
     *            缓存工具类
     * @return 前台输出
     */
    protected R exportExcel(Long exportId, ConfigurationService configurationService, BaseService<?> service,
        RedisUtil redisUtil) {
        Map<String, Object> jsonMap = new HashMap<>(2);
        jsonMap.put("status", ImportOrExportStatusEnum.START.getValue());
        if (exportId == null) {
            Map<String, JSONObject> configurationMap = configurationService.mapAll();
            SystemUser currentSystemUser =
                (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            exportId = IdWorker.getId();
            redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId,
                ImportOrExportStatusEnum.START.getValue(), 20, TimeUnit.MINUTES);
            service.listCorrelationExportExcel(currentSystemUser, configurationMap, exportId);
        }
        jsonMap.put("status", redisUtil.get(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId));
        jsonMap.put("exportId", exportId.toString());
        return R.ok(jsonMap);
    }
}
