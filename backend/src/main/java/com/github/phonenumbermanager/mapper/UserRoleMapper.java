package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.UserRole;

/**
 * 用户角色DAO接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface UserRoleMapper extends CommonMapper<UserRole> {
    /**
     * 查询所有角色与所属系统用户
     *
     * @return 所有角色与所属系统用户
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<UserRole> selectAndSystemUsers() throws DataAccessException;

    /**
     * 查询所有角色与所属系统用户
     *
     * @param page
     *            分页对象
     * @return 所有角色与所属系统用户
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<UserRole> selectAndSystemUsers(Page<UserRole> page) throws DataAccessException;

    /**
     * 查询所有角色与所属权限
     *
     * @return 所有角色与所属权限
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<UserRole> selectAndPrivileges() throws DataAccessException;

    /**
     * 查询所有角色与所属权限
     *
     * @param page
     *            分页对象
     * @return 所有角色与所属权限
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<UserRole> selectAndPrivileges(Page<UserRole> page) throws DataAccessException;

    /**
     * 通过角色ID查询所有角色与所属权限
     *
     * @param id
     *            系统角色编号
     * @return 所有角色与所属权限
     * @throws DataAccessException
     *             数据库操作异常
     */
    UserRole selectAndPrivilegesById(Serializable id) throws DataAccessException;

    /**
     * 通过角色ID查询角色名称
     *
     * @param id
     *            系统角色编号
     * @return 系统角色名称
     * @throws DataAccessException
     *             数据库操作异常
     */
    String selectNameById(Serializable id) throws DataAccessException;
}
