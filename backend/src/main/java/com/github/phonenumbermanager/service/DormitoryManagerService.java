package com.github.phonenumbermanager.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.vo.DormitoryManagerSearchVo;

/**
 * 社区楼长Service接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagerService extends BaseService<DormitoryManager> {
    /**
     * 通过社区楼长查找匹配的社区楼长
     *
     * @param companies
     *            登录的系统用户的单位集合
     * @param dormitoryManagerSearchVo
     *            社区楼片长搜索对象
     * @param page
     *            分页对象
     * @return 查找到的社区楼长集合与分页对象
     */
    IPage<DormitoryManager> page(List<Company> companies, DormitoryManagerSearchVo dormitoryManagerSearchVo,
        IPage<DormitoryManager> page);

    /**
     * 获取社区楼长柱状图数据
     *
     * @param companies
     *            正在登录中的系统用户单位集合
     * @param companyId
     *            单位编号
     * @param typeParam
     *            类型参数
     * @return 柱状图数据
     */
    Map<String, Object> getBarChart(List<Company> companies, Long companyId, Boolean typeParam);
}
