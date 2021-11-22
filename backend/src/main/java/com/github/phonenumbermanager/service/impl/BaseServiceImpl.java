package com.github.phonenumbermanager.service.impl;

import java.util.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.service.BaseService;

/**
 * 基础业务实现
 *
 * @param <M>
 *            <T> SERVICE泛型
 * @author 廿二月的天
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long companyId) {
        return null;
    }

    @Override
    public boolean save(List<List<Object>> data, Long streetId, Map<String, Configuration> configurationsMap) {
        return false;
    }

    @Override
    public IPage<T> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize) {
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
    public List<LinkedHashMap<String, Object>> listCorrelationToMap(Long companyId) {
        return null;
    }

    @Override
    public boolean removeCorrelationById(Long id) {
        return false;
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
}
