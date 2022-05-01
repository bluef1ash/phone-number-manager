package com.github.phonenumbermanager.service;

import java.util.List;
import java.util.Map;

import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.DormitoryManager;

/**
 * 社区楼长Service接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagerService extends BaseService<DormitoryManager> {
    /**
     * 获取社区楼长柱状图数据
     *
     * @param companies
     *            正在登录中的系统用户单位集合
     * @param companyIds
     *            单位编号集合
     * @param typeParam
     *            类型参数
     * @return 柱状图数据
     */
    Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, Boolean typeParam);
}
