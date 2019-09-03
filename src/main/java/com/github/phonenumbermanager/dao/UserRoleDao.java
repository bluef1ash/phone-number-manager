package com.github.phonenumbermanager.dao;

import com.github.phonenumbermanager.entity.UserRole;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.List;

/**
 * 用户角色DAO接口
 *
 * @author 廿二月的天
 */
public interface UserRoleDao extends BaseDao<UserRole> {
    /**
     * 查询所有角色与所属系统用户
     *
     * @return 所有角色与所属系统用户
     * @throws DataAccessException 数据库操作异常
     */
    List<UserRole> selectAndSystemUsers() throws DataAccessException;

    /**
     * 查询所有角色与所属权限
     *
     * @return 所有角色与所属权限
     * @throws DataAccessException 数据库操作异常
     */
    List<UserRole> selectAndPrivileges() throws DataAccessException;

    /**
     * 通过角色ID查询所有角色与所属权限
     *
     * @param id 系统角色编号
     * @return 所有角色与所属权限
     * @throws DataAccessException 数据库操作异常
     */
    UserRole selectAndPrivilegesById(Serializable id) throws DataAccessException;

    /**
     * 通过角色ID查询角色名称
     *
     * @param id 系统角色编号
     * @return 系统角色名称
     * @throws DataAccessException 数据库操作异常
     */
    String selectNameById(Serializable id) throws DataAccessException;
}
