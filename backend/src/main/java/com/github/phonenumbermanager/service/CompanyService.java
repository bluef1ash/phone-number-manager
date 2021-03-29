package com.github.phonenumbermanager.service;

import java.io.Serializable;

import com.github.phonenumbermanager.entity.Company;

/**
 * 单位业务接口
 *
 * @author 廿二月的天
 */
public interface CompanyService extends BaseService<Company> {
    /**
     * 清除缓存系统权限并保存单位
     *
     * @param entity
     *            需要保存的单位实体
     * @param currentSystemUserId
     *            当前登录的系统用户
     * @return 是否成功
     */
    boolean save(Company entity, Long currentSystemUserId);

    /**
     * 清除缓存系统权限并更改单位
     *
     * @param entity
     *            需要更改的单位实体
     * @param currentSystemUserId
     *            当前登录的系统用户
     * @return 是否成功
     */
    boolean updateById(Company entity, Long currentSystemUserId);

    /**
     * 清除缓存系统权限并删除单位
     *
     * @param id
     *            需要删除的单位编号
     * @param currentSystemUserId
     *            当前登录的系统用户
     * @return 是否成功
     */
    boolean removeById(Serializable id, Long currentSystemUserId);
}
