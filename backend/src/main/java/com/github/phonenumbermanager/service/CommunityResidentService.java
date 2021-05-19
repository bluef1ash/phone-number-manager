package com.github.phonenumbermanager.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.util.ExcelUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 社区居民业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过社区居民编号查找社区居民与所属社区
     *
     * @param id 社区居民编号
     * @return 查找到的社区居民
     */
    @Override
    CommunityResident getCorrelation(Serializable id);

    /**
     * 通过社区居民查找匹配的社区居民
     *
     * @param systemUser             登录的系统用户对象
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param communityResident      需要查找的社区居民
     * @param companyId              查找的范围单位的编号
     * @param companyType            查找的范围单位的类别编号
     * @param pageNumber             分页页码
     * @param pageDataSize           每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     */
    IPage<CommunityResident> get(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, CommunityResident communityResident, Serializable companyId, Serializable companyType, Integer pageNumber, Integer pageDataSize);

    /**
     * 通过系统用户角色编号与定位角色编号查找社区居民及所属社区
     *
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param userData               用户数据
     * @return 社区居民与所属社区集合转换的JSON对象
     */
    JSONArray getCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData);

    /**
     * 设置Excel头部
     *
     * @param titles 标题数组
     * @return 设置接口
     */
    ExcelUtil.DataHandler setExcelHead(String[] titles);
}
