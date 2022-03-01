package com.github.phonenumbermanager.service.impl;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.service.BaseService;
import com.github.phonenumbermanager.vo.SelectListVo;

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
        return saveBatch(entityList, 1000);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        if (entityList.size() == 1) {
            return executeBatch(entityList, batchSize,
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
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        return null;
    }

    @Override
    public boolean save(List<List<Object>> data, Long streetId, Map<String, JSONObject> configurationMap) {
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
    public Map<String, Object> getBarChart(List<Company> companies, Long companyId) {
        return null;
    }

    @Override
    public ExcelWriter listCorrelationExportExcel(List<Company> companies, Map<String, JSONObject> configurationMap) {
        return null;
    }

    @Override
    public List<SelectListVo> treeSelectList() {
        return null;
    }

    /**
     * 柱状图数据处理
     *
     * @param label
     *            饼图图例
     * @param companyLabel
     *            柱状图横坐标文字
     * @param formatter
     *            格式文字
     * @param object
     *            饼图数据内容
     * @return 处理后的对象
     */
    protected Map<String, Object> barChartDataHandler(String label, String companyLabel, String formatter,
        List<Map<String, Object>> object) {
        Map<String, Object> barChartMap = new HashMap<>(3);
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> data = new HashMap<>(3);
        List<String> titleLabel = new ArrayList<>();
        for (Map<String, Object> residentCount : object) {
            Map<String, Object> row = new HashMap<>(3);
            row.put(companyLabel, residentCount.get("name"));
            titleLabel.add((String)residentCount.get("name"));
            row.put(label, residentCount.get("value"));
            rows.add(row);
        }
        columns.add(companyLabel);
        columns.add(label);
        barChartMap.put("columns", columns);
        barChartMap.put("rows", rows);
        data.put("data", barChartMap);
        data.put("titleLabel", titleLabel);
        data.put("formatter", formatter);
        return data;
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
