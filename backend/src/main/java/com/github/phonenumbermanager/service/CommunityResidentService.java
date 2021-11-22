package com.github.phonenumbermanager.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.vo.CommunityResidentSearchVo;

/**
 * 社区居民业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过社区居民查找匹配的社区居民
     *
     * @param companies
     *            登录的系统用户单位对象集合
     * @param communityResidentSearchVo
     *            社区居民搜索视图对象
     * @param page
     *            分页对象
     * @return 查找到的社区居民集合与分页对象
     */
    IPage<CommunityResident> page(List<Company> companies, CommunityResidentSearchVo communityResidentSearchVo,
        IPage<CommunityResident> page);
}
