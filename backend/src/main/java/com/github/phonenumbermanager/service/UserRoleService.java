package com.github.phonenumbermanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.UserRole;

import java.io.Serializable;

/**
 * 系统用户角色业务接口
 *
 * @author 廿二月的天
 */
public interface UserRoleService extends BaseService<UserRole> {

    /**
     * 查找所有角色与所属权限（包含分页）
     *
     * @param pageNumber   分页页码
     * @param pageDataSize 每页显示的条目数
     * @return 查找到的所有角色与所属系统权限与分页对象
     */
    IPage<UserRole> getAndPrivileges(Integer pageNumber, Integer pageDataSize);

    /**
     * 通过角色编号查找角色与所属权限
     *
     * @param id 系统用户角色编号
     * @return 查找到的系统用户角色对象
     */
    @Override
    UserRole getCorrelation(Serializable id);
}
