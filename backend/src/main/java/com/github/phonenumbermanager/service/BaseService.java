package com.github.phonenumbermanager.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.json.JSONObject;

/**
 * Service层基本接口
 *
 * @param <T>
 *            SERVICE接口泛型
 * @author 廿二月的天
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 获取录入统计信息
     *
     * @param companies
     *            当前登录系统用户单位集合
     * @param companyId
     *            需要获取数据的单位编号
     * @return 统计信息对象
     */
    Map<String, Object> getBaseMessage(List<Company> companies, Long companyId);

    /**
     * 关联查找（包含分页）
     *
     * @param companies
     *            当前已登录的用户对象所属单位集合
     * @param pageNumber
     *            分页页码
     * @param pageDataSize
     *            每页显示的条目数
     * @param search
     *            搜索条件
     * @param sort
     *            排序条件
     * @return 查找到的所有对象与所属对象与分页对象
     */
    IPage<T> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize, JSONObject search,
        JSONObject sort);

    /**
     * 获取柱状图数据
     *
     * @param companies
     *            正在登录中的系统用户单位集合
     * @param companyId
     *            需要获取的单位编号
     * @return 柱状图数据
     */
    Map<String, Object> getBarChart(List<Company> companies, Long companyId);

    /**
     * 从Excel导入数据
     *
     * @param data
     *            Excel数据
     * @param streetId
     *            导入的街道编号
     * @param configurationMap
     *            系统配置
     * @return 导入的行数
     */
    boolean save(List<List<Object>> data, Long streetId, Map<String, JSONObject> configurationMap);

    /**
     * 通过编号关联查找
     *
     * @param id
     *            需要查找的对象编号
     * @return 对应的对象
     */
    T getCorrelation(Long id);

    /**
     * 通过单位编号查找社区居民或楼片长及所属社区
     *
     * @param companyId
     *            单位编号
     * @return 社区居民与所属社区集合转换的JSON对象
     */
    List<LinkedHashMap<String, Object>> listCorrelationToMap(Long companyId);

    /**
     * 通过编号关联删除
     *
     * @param id
     *            需要删除的编号
     * @return 删除是否成功
     */
    boolean removeCorrelationById(Long id);

    /**
     * 获取树形表单列表
     *
     * @return 树形表单列表集合
     */
    List<SelectListVo> treeSelectList();
}
