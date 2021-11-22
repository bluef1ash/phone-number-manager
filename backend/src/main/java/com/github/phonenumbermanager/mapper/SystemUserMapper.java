package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.SystemUser;

/**
 * 系统用户Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUser> {
    /**
     * 查询所有系统用户与所属角色
     *
     * @return 所有系统用户与所属角色
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<SystemUser> selectAndCompanies() throws DataAccessException;

    /**
     * 查询所有系统用户与所属联系方式
     *
     * @return 所有系统用户与所属联系方式
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<SystemUser> selectAndPhoneNumber() throws DataAccessException;

    /**
     * 分页查询所有系统用户与所属单位
     *
     * @param companyIds
     *            当前已登录系统用户所属单位编号集合
     * @param page
     *            分页对象
     * @return 所有系统用户与所属角色
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<SystemUser> selectCorrelationByCompanyIds(List<Long> companyIds, Page<SystemUser> page)
        throws DataAccessException;

    /**
     * 通过联系方式查询系统用户
     *
     * @param phoneNumber
     *            系统用户联系方式
     * @return 查询到的系统用户与所属角色以及权限
     * @throws DataAccessException
     *             数据库操作异常
     */
    SystemUser selectAndCompaniesByPhoneNumber(String phoneNumber) throws DataAccessException;

    /**
     * 通过单位编号查询
     *
     * @param companyId
     *            单位编号
     * @return 查询到的系统用户
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<SystemUser> selectByCompanyId(Serializable companyId) throws DataAccessException;
}
