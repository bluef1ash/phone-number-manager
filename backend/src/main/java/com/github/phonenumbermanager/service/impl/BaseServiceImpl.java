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
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;
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
    public boolean save(List<List<Object>> data, Map<String, Configuration> configurationMap) {
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
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, List<Company> companyAll,
        String personCountAlias) {
        List<Company> companyList = new ArrayList<>();
        List<Long> subordinateCompanyIds = new ArrayList<>();
        companiesAndSubordinate(companies, companyIds, companyAll, companyList, subordinateCompanyIds);
        if (subordinateCompanyIds.isEmpty()) {
            subordinateCompanyIds = companies.stream().map(Company::getId).collect(Collectors.toList());
        }
        return barChartHandle(companyAll, personCountAlias, companyList, subordinateCompanyIds);
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(SystemUser currentSystemUser,
        Map<String, Configuration> configurationMap) {
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
        Map<String, Configuration> configurationMap, List<Company> companyAll) {
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").getContent());
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

    /**
     * 前端传入数据处理
     *
     * @param companies
     *            当前系统用户单位集合
     * @param companyIds
     *            传入单位编号数组
     * @return 所有单位
     */
    protected Long[] frontendUserRequestCompanyHandle(List<Company> companies, List<Company> companyAll,
        Long[] companyIds) {
        Long[] systemUserCompanyParentIds;
        if (companyIds == null || companyIds.length == 0) {
            if (companies == null) {
                systemUserCompanyParentIds = companyAll.stream().filter(company -> company.getParentId() == 0L)
                    .map(Company::getId).toArray(Long[]::new);
            } else {
                systemUserCompanyParentIds = companies.stream().map(Company::getId).toArray(Long[]::new);
            }
        } else {
            systemUserCompanyParentIds = companyIds;
        }
        return systemUserCompanyParentIds;
    }

    /**
     * 获取柱状图图示
     *
     * @param xFieldString
     *            X轴字符串
     * @param xFieldStringAlias
     *            X轴别名
     * @param yFieldString
     *            Y轴字符串
     * @param yFieldStringAlias
     *            Y轴别名
     * @return 柱状图变量
     */
    protected Map<String, Object> getBarChartMap(String xFieldString, String xFieldStringAlias, String yFieldString,
        String yFieldStringAlias) {
        Map<String, Object> barChart = new HashMap<>(3);
        Map<String, Object> meta = new HashMap<>(2);
        Map<String, Object> xFieldStringAliasMap = new HashMap<>(1);
        Map<String, Object> yFieldStringAliasMap = new HashMap<>(1);
        xFieldStringAliasMap.put("alias", xFieldStringAlias);
        yFieldStringAliasMap.put("alias", yFieldStringAlias);
        meta.put(xFieldString, xFieldStringAliasMap);
        meta.put(yFieldString, yFieldStringAliasMap);
        barChart.put("xField", xFieldString);
        barChart.put("yField", yFieldString);
        barChart.put("meta", meta);
        barChart.put("loading", false);
        return barChart;
    }

    /**
     * 柱状图图示处理
     *
     * @param companyAll
     *            全部单位集合
     * @param personCountAlias
     *            员工数别名
     * @param companyList
     *            单位集合
     * @param subordinateCompanyIds
     *            下级单位编号集合
     * @return 柱状图对象
     */
    protected Map<String, Object> barChartHandle(List<Company> companyAll, String personCountAlias,
        List<Company> companyList, List<Long> subordinateCompanyIds) {
        Map<BigInteger, Map<String, Long>> dataMap = baseMapper.selectCountForGroupCompany(subordinateCompanyIds);
        List<Map<String, Object>> data = new ArrayList<>();
        if (!dataMap.isEmpty()) {
            if (companyList.isEmpty()) {
                Map<String, Object> counter = new HashMap<>(2);
                Long id = subordinateCompanyIds.get(0);
                counter.put("companyName", companyAll.stream().filter(company -> company.getId().equals(id))
                    .map(Company::getName).findFirst().orElse(""));
                counter.put("personCount", dataMap.get(Convert.toBigInteger(id)).get("person_count"));
                data.add(counter);
            } else {
                for (Company company : companyList) {
                    Map<String, Object> counter = new HashMap<>(2);
                    long count = 0L;
                    List<Long> subCompanyIds = CommonUtil.listRecursionCompanyIds(companyAll, company.getId());
                    if (subCompanyIds.size() > 0) {
                        for (Long subCompanyId : subCompanyIds) {
                            BigInteger id = BigInteger.valueOf(subCompanyId);
                            if (dataMap.get(id) != null) {
                                count += dataMap.get(id).get("person_count");
                            }
                        }
                    } else {
                        BigInteger id = BigInteger.valueOf(company.getId());
                        if (dataMap.get(id) != null) {
                            count = dataMap.get(id).get("person_count");
                        }
                    }
                    counter.put("companyName", company.getName());
                    counter.put("personCount", count);
                    data.add(counter);
                }
            }
        }
        Map<String, Object> barChart = getBarChartMap("companyName", "单位", "personCount", personCountAlias);
        barChart.put("data", data);
        return barChart;
    }

    /**
     * 获取单位集合与下级单位编号集合
     *
     * @param companies
     *            用户所属单位
     * @param companyIds
     *            要查找的单位编号数组
     * @param companyAll
     *            所有单位集合
     * @param companyList
     *            需要返回的单位集合
     * @param subordinateCompanyIds
     *            需要返回的下级单位编号集合
     */
    protected void companiesAndSubordinate(List<Company> companies, Long[] companyIds, List<Company> companyAll,
        List<Company> companyList, List<Long> subordinateCompanyIds) {
        for (Long companyId : frontendUserRequestCompanyHandle(companies, companyAll, companyIds)) {
            subordinateCompanyIds.addAll(CommonUtil.listRecursionCompanies(companyAll, companyId).stream()
                .map(Company::getId).collect(Collectors.toList()));
            companyList.addAll(companyAll.stream().filter(company -> companyId.equals(company.getParentId()))
                .collect(Collectors.toList()));
        }
    }

    /**
     * 获取单位集合与下级单位编号集合
     *
     * @param companies
     *            用户所属单位
     * @param companyAll
     *            所有单位集合
     * @return 单位集合与下级单位编号集合
     */
    protected List<Company> systemUserCompanyHandler(List<Company> companies, List<Company> companyAll) {
        List<Company> companyList = null;
        if (companies != null && !companies.isEmpty()) {
            companyList =
                companies.stream().map(company -> CommonUtil.listRecursionCompanies(companyAll, company.getId()))
                    .flatMap(List::stream).distinct().collect(Collectors.toList());
            companyList.addAll(companies);
        }
        return companyList;
    }

    protected List<SystemPermission> getPrevSystemPermissions(SystemPermissionMapper systemPermissionMapper,
        Boolean display, List<Long> companyIds, List<Company> companyAll) {
        List<SystemPermission> systemPermissions;
        do {
            systemPermissions = systemPermissionMapper.selectListByCompanyIds(companyIds, display);
            if (companyIds == null || companyIds.isEmpty()) {
                break;
            }
            companyIds = companyIds.stream()
                .map(companyId -> companyAll.stream().filter(company -> companyId.equals(company.getId())).findFirst()
                    .orElse(null))
                .filter(Objects::nonNull).map(Company::getParentId)
                .map(parentId -> companyAll.stream().filter(c -> parentId.equals(c.getId())).findFirst().orElse(null))
                .filter(Objects::nonNull).map(Company::getId).collect(Collectors.toList());
        } while (systemPermissions.isEmpty());
        return systemPermissions;
    }
}
