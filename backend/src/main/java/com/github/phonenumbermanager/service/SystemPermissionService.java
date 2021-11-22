package com.github.phonenumbermanager.service;

import java.util.List;

import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.SystemPermission;

/**
 * 系统权限业务接口
 *
 * @author 廿二月的天
 */
public interface SystemPermissionService extends BaseService<SystemPermission> {
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
     * @param display
     *            是否在导航栏中显示
     * @param companies
     *            当前登录的用户所属单位集合
     * @return 查找到的系统用户权限对象的集合
     */
    List<SystemPermission> listMenu(Boolean display, List<Company> companies);
}
