package com.github.phonenumbermanager.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;

/**
 * 基础业务实现
 *
 * @param <M>
 *            Mapper 对象
 * @param <T>
 *            实体对象
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
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, List<Company> companyAll,
        String personCountAlias) {
        List<Map<String, Object>> groups = groupsCountData(companies, companyIds, companyAll, false);
        List<Map<String, Object>> data =
            groups.stream().map(group -> personCountDataHandler(companyAll, group)).collect(Collectors.toList());
        Map<String, Object> barChart = getBarChartMap("companyName", "单位", personCountAlias);
        barChart.put("data", data);
        return barChart;
    }

    /**
     * 设置 Excel 表头和内容样式
     *
     * @return 水平单元格样式策略
     */
    protected HorizontalCellStyleStrategy getHeadAndContentStyle() {
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("仿宋_GB2312");
        contentWriteFont.setFontHeightInPoints((short)9);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteFont headFont = new WriteFont();
        headFont.setFontName("黑体");
        headFont.setFontHeightInPoints((short)12);
        headWriteCellStyle.setWriteFont(headFont);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
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
     * 导出 Excel 文件获取下级单位编号
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
     *            X 轴字符串
     * @param xFieldStringAlias
     *            X 轴别名
     * @param yFieldStringAlias
     *            Y 轴别名
     * @return 柱状图变量
     */
    protected Map<String, Object> getBarChartMap(String xFieldString, String xFieldStringAlias,
        String yFieldStringAlias) {
        Map<String, Object> barChart = new HashMap<>(3);
        Map<String, Object> meta = new HashMap<>(2);
        Map<String, Object> xFieldStringAliasMap = new HashMap<>(1);
        Map<String, Object> yFieldStringAliasMap = new HashMap<>(1);
        xFieldStringAliasMap.put("alias", xFieldStringAlias);
        yFieldStringAliasMap.put("alias", yFieldStringAlias);
        meta.put(xFieldString, xFieldStringAliasMap);
        meta.put("personCount", yFieldStringAliasMap);
        barChart.put("xField", xFieldString);
        barChart.put("yField", "personCount");
        barChart.put("meta", meta);
        barChart.put("loading", false);
        return barChart;
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

    /**
     * 处理分组统计柱状图表数据
     *
     * @param companies
     *            系统用户所属单位集合
     * @param companyIds
     *            前端传入单位编号数组
     * @param companyAll
     *            所有单位集合
     * @param isUseLeaf
     *            是否使用叶子单位统计
     * @return 处理分组统计柱状图表 Map 数据
     */
    protected List<Map<String, Object>> groupsCountData(List<Company> companies, Long[] companyIds,
        List<Company> companyAll, boolean isUseLeaf) {
        Stream<Company> companyStream;
        if (companyIds == null) {
            // 默认以用户单位统计
            if (companies == null) {
                // 管理员
                companies =
                    companyAll.stream().filter(company -> company.getParentId() == 0L).collect(Collectors.toList());
            }
            // 构建树，计算树枝末端社区居民数量
            companyStream = companies.stream();
        } else {
            if (isUseLeaf) {
                companyStream = Arrays.stream(companyIds)
                    .flatMap(companyId -> companyAll.stream().filter(company -> companyId.equals(company.getId())));
            } else {
                companyStream = Arrays.stream(companyIds).flatMap(
                    companyId -> companyAll.stream().filter(company -> companyId.equals(company.getParentId())));
            }
        }
        List<Long> grassCompanyIds = new ArrayList<>();
        Map<Long, List<Long>> notGrassCompanyIds = new LinkedHashMap<>();
        companyStream.forEach(company -> {
            List<Long> ids = CommonUtil.listRecursionCompanyIds(companyAll, company.getId());
            if (ids.isEmpty()) {
                grassCompanyIds.add(company.getId());
            } else {
                notGrassCompanyIds.put(company.getId(), ids);
            }
        });
        List<Map<String, Object>> groups = new ArrayList<>();
        if (!grassCompanyIds.isEmpty()) {
            groups.addAll(baseMapper.selectCountForGroupCompany(grassCompanyIds));
        }
        if (!notGrassCompanyIds.isEmpty()) {
            groups.addAll(baseMapper.selectCountForSubQueryCompany(notGrassCompanyIds));
        }
        return groups;
    }

    /**
     * 人员统计数据处理
     *
     * @param companyAll
     *            所有单位集合
     * @param value
     *            数据库返回的数据
     * @return 处理后的格式
     */
    protected static Map<String, Object> personCountDataHandler(List<Company> companyAll, Map<String, Object> value) {
        String companyName =
            companyAll.stream().filter(company -> Convert.toLong(value.get("company_id")).equals(company.getId()))
                .map(Company::getName).findFirst().orElse("");
        Map<String, Object> counter = new HashMap<>(2);
        counter.put("companyName", companyName);
        counter.put("personCount", value.get("person_count"));
        return counter;
    }

    /**
     * 通过基层单位编号集合获取数据
     *
     * @param subordinateCompanyIds
     *            基层单位编号集合
     * @return 数据集合
     */
    protected List<T> getListByCompanyIds(List<Long> subordinateCompanyIds) {
        int currentPage = 1;
        Page<T> page = new Page<>(currentPage, SystemConstant.READ_DATABASE_SIZE);
        IPage<T> iPage = baseMapper.selectListByCompanyIds(subordinateCompanyIds, page);
        List<T> objects = iPage.getRecords();
        if (iPage.getTotal() > SystemConstant.READ_DATABASE_SIZE) {
            for (int i = currentPage + 1; i <= Math.ceil((double)iPage.getTotal() / SystemConstant.READ_DATABASE_SIZE);
                i++) {
                page.setCurrent(i);
                iPage = baseMapper.selectListByCompanyIds(subordinateCompanyIds, page);
                objects.addAll(iPage.getRecords());
            }
        }
        return objects;
    }
}
