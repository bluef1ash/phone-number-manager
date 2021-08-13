package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.entity.UserPrivilege;

/**
 * 用户权限Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface UserPrivilegeMapper extends CommonMapper<UserPrivilege> {
    /**
     * 通过角色ID查询
     *
     * @param roleId
     *            系统用户角色编号
     * @return 系统用户权限列表
     * @throws DataAccessException
     *             数据库操作异常
     */
    Set<UserPrivilege> selectByRoleId(Serializable roleId) throws DataAccessException;

    /**
     * 通过角色是否在导航栏显示查询
     *
     * @param display
     *            是否在导航栏中显示
     * @return 系统用户权限列表
     * @throws DataAccessException
     *             数据库操作异常
     */
    Set<UserPrivilege> selectByDisplay(Boolean display) throws DataAccessException;

    /**
     * 通过角色是否在导航栏和拥有的权限显示查询
     *
     * @param display
     *            是否在菜单中显示
     * @param constraintAuthes
     *            系统应用集合
     * @return 系统用户权限列表
     * @throws DataAccessException
     *             数据库操作异常
     */
    Set<UserPrivilege> selectByDisplayAndConstraintAuth(@Param("display") Boolean display,
        @Param("constraintAuthes") Set<String> constraintAuthes) throws DataAccessException;

    /**
     * 通过角色父级ID查询
     *
     * @param parentId
     *            父级权限编号
     * @param display
     *            是否在导航栏中显示
     * @return 系统用户权限集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    Set<UserPrivilege> selectByParentIdAndDisplay(Serializable parentId, Boolean display) throws DataAccessException;

    /**
     * 通过角色父级ID查询
     *
     * @param parentId
     *            父级权限编号
     * @return 系统用户权限集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    Set<UserPrivilege> selectByParentId(Serializable parentId) throws DataAccessException;
}
