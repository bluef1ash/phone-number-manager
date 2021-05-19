package com.github.phonenumbermanager.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.SystemUser;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户Mapper接口
 *
 * @author 廿二月的天
 */
@Repository
public interface SystemUserMapper extends CommonMapper<SystemUser> {
    /**
     * 查询所有系统用户与所属角色
     *
     * @return 所有系统用户与所属角色
     * @throws DataAccessException 数据库操作异常
     */
    List<SystemUser> selectAndRoles() throws DataAccessException;

    /**
     * 分页查询所有系统用户与所属角色
     *
     * @param page 分页对象
     * @return 所有系统用户与所属角色
     * @throws DataAccessException 数据库操作异常
     */
    IPage<SystemUser> selectAndRoles(Page<SystemUser> page) throws DataAccessException;

    /**
     * 通过用户编号查询系统用户与所属角色
     *
     * @param id 用户编号
     * @return 查询到的系统用户与所属角色
     * @throws DataAccessException 数据库操作异常
     */
    SystemUser selectAndRoleById(Serializable id) throws DataAccessException;

    /**
     * 通过用户名称查询系统用户与所属角色以及权限
     *
     * @param username 系统用户名称
     * @return 查询到的系统用户与所属角色以及权限
     * @throws DataAccessException 数据库操作异常
     */
    SystemUser selectAndRoleAndPrivilegesByName(String username) throws DataAccessException;

    /**
     * 查询系统用户（不含密码）
     *
     * @return 查询到的系统用户
     * @throws DataAccessException 数据库操作异常
     */
    List<SystemUser> selectIdAndName() throws DataAccessException;

    /**
     * 通过系统用户角色编号查询
     *
     * @param roleId 系统用户角色编号
     * @return 查询到的系统用户
     * @throws DataAccessException 数据库操作异常
     */
    List<SystemUser> selectByRoleId(Serializable roleId) throws DataAccessException;

    /**
     * 通过系统用户角色编号统计
     *
     * @param roleId 系统用户角色编号
     * @return 统计到的系统用户数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countByRoleId(Serializable roleId) throws DataAccessException;
}
