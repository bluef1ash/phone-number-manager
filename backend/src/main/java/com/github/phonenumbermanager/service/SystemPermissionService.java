package com.github.phonenumbermanager.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemPermission;

/**
 * 系统权限业务接口
 *
 * @author 廿二月的天
 */
public interface SystemPermissionService extends BaseService<SystemPermission> {
    /**
     * 查找全部系统权限
     *
     * @return 系统用户权限对象的集合
     */
    List<SystemPermission> listAll();

    /**
     * 通过单位编号查找
     *
     * @param companyId
     *            单位编号
     * @return 系统用户权限对象的集合
     */
    List<SystemPermission> listByCompanyId(Long companyId);

    /**
     * 通过权限是否在导航栏显示查找
     *
     * @param companies
     *            当前登录的用户所属单位集合
     * @param currentSystemUserId
     *            当前登录的用户编号
     * @return 查找到的系统用户权限对象的集合与权限集合
     */
    Map<String, Object> listMenu(List<Company> companies, Long currentSystemUserId);

    /**
     * 分页树形系统权限
     *
     * @param current
     *            分页页码
     * @param pageSize
     *            每页数据数量
     * @param wrapper
     *            搜索条件
     * @return 系统权限分页对象
     */
    IPage<SystemPermission> treePage(Integer current, Integer pageSize, QueryWrapper<SystemPermission> wrapper);
}
