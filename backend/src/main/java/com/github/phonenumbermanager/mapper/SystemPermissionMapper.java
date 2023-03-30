package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.entity.SystemPermission;

/**
 * 系统用户权限 Mapper 接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface SystemPermissionMapper extends BaseMapper<SystemPermission> {
    /**
     * 通过单位编号查询
     *
     * @param companyId
     *            单位编号
     * @return 系统用户权限列表
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<SystemPermission> selectByCompanyId(Serializable companyId) throws DataAccessException;

    /**
     * 通过是否在导航栏中显示查询
     *
     * @param isDisplay
     *            是否在导航栏中显示
     * @return 社区楼长集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<SystemPermission> selectListByIsDisplay(Boolean isDisplay) throws DataAccessException;
}
