package com.github.phonenumbermanager.service.impl;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.style.StyleUtil;

/**
 * 基础业务实现
 *
 * @param <M>
 *            <T> SERVICE泛型
 * @author 廿二月的天
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        if (entityList.size() == 1) {
            return executeBatch(entityList, 1,
                (sqlSession, entity) -> sqlSession.insert(getSqlStatement(SqlMethod.INSERT_ONE), entity));
        }
        return getBaseMapper().insertBatchSomeColumn(entityList) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveIgnore(T entity) {
        return getBaseMapper().insertIgnore(entity) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveIgnoreBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        if (entityList.size() == 1) {
            return this.executeBatch(entityList, batchSize,
                (sqlSession, entity) -> getBaseMapper().insertIgnore(entity));
        }
        return baseMapper.insertIgnoreBatchSomeColumn(entityList) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveIgnoreBatch(Collection<T> entityList) {
        return saveIgnoreBatch(entityList, 1000);
    }

    @Override
    public boolean saveCorrelation(SystemUser entity) {
        return false;
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long[] companyIds) {
        return null;
    }

    @Override
    public boolean save(List<List<Object>> data, Map<String, JSONObject> configurationMap) {
        return false;
    }

    @Override
    public IPage<T> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        return null;
    }

    @Override
    public T getCorrelation(Long id) {
        return null;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds) {
        return null;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, Boolean typeParam) {
        return null;
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(SystemUser currentSystemUser,
        Map<String, JSONObject> configurationMap) {
        return null;
    }

    @Override
    public List<SelectListVo> treeSelectList(Long[] parentIds) {
        return null;
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
     * 获取最下级单位编号
     *
     * @param companies
     *            登录的系统用户单位集合
     * @return 最下级单位编号集合
     */
    protected List<Long> getSubordinateCompanyIds(List<Company> companies, List<Company> companyAll) {
        List<Long> subCompanyIds = new ArrayList<>();
        List<Company> companyList = new ArrayList<>();
        companies.forEach(company -> {
            companyList.add(company);
            companyList.addAll(CommonUtil.listRecursionCompanies(companyAll, company.getId()));
        });
        Map<Long, List<Company>> groupByParentIdMap =
            companyList.stream().collect(Collectors.groupingBy(Company::getParentId));
        companyAll.forEach(company -> {
            if (CollUtil.isEmpty(groupByParentIdMap.get(company.getId()))) {
                subCompanyIds.add(company.getId());
            } else {
                CommonUtil.listSubmissionCompanyIds(subCompanyIds, company.getId(), groupByParentIdMap);
            }
        });
        return subCompanyIds;
    }

    /**
     * 柱状图数据处理
     *
     * @param companyAll
     *            所有单位集合
     * @param companyIds
     *            查询单位编号数组
     * @param barChart
     *            数据
     * @return 处理后的对象
     */
    protected Map<String, Object> barChartDataHandler(List<Company> companyAll, Long[] companyIds,
        Map<String, Object> barChart) {
        List<Company> companies = new ArrayList<>();
        List<Long> subordinateCompanyIds = new ArrayList<>();
        for (Long companyId : companyIds) {
            subordinateCompanyIds.addAll(CommonUtil.listRecursionCompanies(companyAll, companyId).stream()
                .map(Company::getId).collect(Collectors.toList()));
            companies.addAll(companyAll.stream().filter(company -> companyId.equals(company.getParentId()))
                .collect(Collectors.toList()));
        }
        Map<BigInteger, Map<String, Long>> dataMap = baseMapper.countForGroupCompany(subordinateCompanyIds);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Company company : companies) {
            Map<String, Object> counter = new HashMap<>(2);
            long count = 0L;
            List<Long> subCompanyIds = CommonUtil.listRecursionCompanyIds(companyAll, company.getId());
            if (subCompanyIds.size() > 0) {
                for (Long subCompanyId : subCompanyIds) {
                    BigInteger id = BigInteger.valueOf(subCompanyId);
                    if (dataMap.get(id) != null) {
                        count += dataMap.get(id).get("personCount");
                    }
                }
            } else {
                BigInteger id = BigInteger.valueOf(company.getId());
                count = dataMap.get(id).get("personCount");
            }
            counter.put("companyName", company.getName());
            counter.put("personCount", count);
            data.add(counter);
        }
        barChart.put("data", data);
        return barChart;
    }

    /**
     * 导出Excel文件获取下级单位编号
     *
     * @param currentSystemUser
     *            当前已登录的系统用户
     * @param configurationMap
     *            系统配置项
     * @param companyAll
     *            所有单位集合
     * @return 下级单位编号
     */
    protected List<Long> exportExcelGetSubordinateCompanyIds(SystemUser currentSystemUser,
        Map<String, JSONObject> configurationMap, List<Company> companyAll) {
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        List<Company> companies;
        if (systemAdministratorId.equals(currentSystemUser.getId()) && currentSystemUser.getCompanies() == null) {
            companies = companyAll.stream().filter(company -> company.getParentId() == 0L).collect(Collectors.toList());
        } else {
            companies = currentSystemUser.getCompanies();
        }
        return getSubordinateCompanyIds(companies, companyAll);
    }

    /**
     * 导出Excel文件标题处理
     *
     * @param excelWriter
     *            Excel写入对象
     * @param title
     *            标题
     * @param mergeSize
     *            合并单元格大小
     */
    protected void exportExcelTitleHandle(ExcelWriter excelWriter, String title, Integer mergeSize) {
        excelWriter.passCurrentRow();
        CellStyle titleStyle = excelWriter.getOrCreateCellStyle(0, excelWriter.getCurrentRow());
        setCellStyle(titleStyle, excelWriter, "方正小标宋简体", (short)16, false, false, false);
        excelWriter.merge(excelWriter.getCurrentRow(), excelWriter.getCurrentRow(), 0, mergeSize, title, titleStyle);
        excelWriter.passCurrentRow();
    }
}
