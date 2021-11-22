package com.github.phonenumbermanager.service;

import java.util.List;
import java.util.Map;

import com.github.phonenumbermanager.entity.Company;

/**
 * 单位业务接口
 *
 * @author 廿二月的天
 */
public interface CompanyService extends BaseService<Company> {
    /**
     * 通过单位对象集合获取最下级单位编号
     *
     * @param companyIds
     *            取下级单位编号集合
     * @param companies
     *            单位对象集合
     * @param companyAll
     *            所有单位对象集合
     * @param parents
     *            上级单位
     */
    void listSubmissionCompanyIds(List<Long> companyIds, List<Company> companies, List<Company> companyAll,
        Map<Long, String> parents);

    /**
     * 递归获取链条单位编号
     *
     * @param companyIds
     *            递归单位编号集合
     * @param companies
     *            单位对象集合
     * @param companyAll
     *            所有单位对象集合
     * @param parentId
     *            上级单位编号
     */
    void listRecursionCompanyIds(List<Long> companyIds, List<Company> companies, List<Company> companyAll,
        Long parentId);
}
