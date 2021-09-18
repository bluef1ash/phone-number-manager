package com.github.phonenumbermanager.mapper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;

/**
 * 基础Mapper接口
 *
 * @param <T>
 *            实体对象类
 * @author 廿二月的天
 */
@Mapper
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 通过实体对象查询对象
     *
     * @param object
     *            实体对象
     * @return 实体对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectByObject(T object) throws DataAccessException;

    /**
     * 查询对象的编号和名称
     *
     * @return 实体对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectForIdAndName() throws DataAccessException;

    /**
     * 通过系统用户数据查询
     *
     * @param userData
     *            系统用户数据
     * @param communityCompanyType
     *            社区用户编号
     * @param subdistrictCompanyType
     *            街道用户编号
     * @return 社区楼长集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectByUserData(@Param("userData") List<Map<String, Object>> userData,
        @Param("communityCompanyType") Serializable communityCompanyType,
        @Param("subdistrictCompanyType") Serializable subdistrictCompanyType) throws DataAccessException;

    /**
     * 通过编号查询对象与所属社区
     *
     * @param id
     *            对象编号
     * @return 对象与所属社区
     * @throws DataAccessException
     *             数据库操作异常
     */
    T selectAndCommunityById(Serializable id) throws DataAccessException;

    /**
     * 通过社区编号查询
     *
     * @param communityId
     *            社区编号
     * @return 查询到的对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectAndCommunityByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 查询所有与所属社区
     *
     * @param communityIds
     *            多个社区编号
     * @param phoneNumberSourceTypeEnum
     *            联系方式来源类型
     * @return 所有与所属社区
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectAndCommunityByCommunityIds(List<Serializable> communityIds,
        PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum) throws DataAccessException;

    /**
     * 查询所有与所属社区（包含分页）
     *
     * @param page
     *            分页对象
     * @param communityIds
     *            多个社区编号
     * @param phoneNumberSourceTypeEnum
     *            联系方式来源类型
     * @return 所有与所属社区
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<T> selectAndCommunityByCommunityIds(Page<T> page, List<Serializable> communityIds,
        PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum) throws DataAccessException;

    /**
     * 通过用户角色查询对象
     *
     * @param companyType
     *            系统用户角色类别
     * @param companyId
     *            系统用户角色定位编号
     * @param systemCompanyType
     *            系统单位类型编号
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @param object
     *            需要查询的对象
     * @return 对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectByUserRole(@Param("companyType") Serializable companyType, @Param("companyId") Serializable companyId,
        @Param("systemCompanyType") Serializable systemCompanyType,
        @Param("communityCompanyType") Serializable communityCompanyType,
        @Param("subdistrictCompanyType") Serializable subdistrictCompanyType, @Param("object") T object)
        throws DataAccessException;

    /**
     * 通过用户角色查询对象（包含分页）
     *
     * @param page
     *            分页对象
     * @param companyType
     *            系统用户角色类别
     * @param companyId
     *            系统用户角色定位编号
     * @param systemCompanyType
     *            系统单位类型编号
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @param object
     *            需要查询的对象
     * @return 对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<T> selectByUserRole(Page<T> page, @Param("companyType") Serializable companyType,
        @Param("companyId") Serializable companyId, @Param("systemCompanyType") Serializable systemCompanyType,
        @Param("communityCompanyType") Serializable communityCompanyType,
        @Param("subdistrictCompanyType") Serializable subdistrictCompanyType, @Param("object") T object)
        throws DataAccessException;

    /**
     * 通过社区编号查询对象的数量
     *
     * @param communityId
     *            社区编号
     * @return 对象的数量
     * @throws DataAccessException
     *             数据库操作异常
     */
    Long countByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过姓名+地址查询姓名与社区编号
     *
     * @param nameAddress
     *            姓名与家庭住址
     * @param id
     *            编号
     * @param subdistrictId
     *            街道办事处编号
     * @return 查询到的对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectByNameAndAddress(@Param("nameAddress") String nameAddress, @Param("id") Serializable id,
        @Param("subdistrictId") Serializable subdistrictId) throws DataAccessException;

    /**
     * 通过电话数组查询姓名与社区编号
     *
     * @param phoneNumbers
     *            联系方式对象集合
     * @param id
     *            编号
     * @param subdistrictId
     *            街道办事处编号
     * @param sourceType
     *            来源类型
     * @return 查询到的对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<T> selectByPhones(List<PhoneNumber> phoneNumbers, Serializable id, Serializable subdistrictId,
        PhoneNumberSourceTypeEnum sourceType) throws DataAccessException;

    /**
     * 街道分组统计数量
     *
     * @return 社区数量对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    @MapKey("name")
    LinkedList<Map<String, Object>> countForGroupSubdistrict() throws DataAccessException;

    /**
     * 通过街道单位编号社区分组统计数量
     *
     * @param subdistrictId
     *            街道编号
     * @return 社区数量对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    @MapKey("name")
    LinkedList<Map<String, Object>> countForGroupCommunity(Serializable subdistrictId) throws DataAccessException;

    /**
     * 统计社区
     *
     * @param communityId
     *            社区编号
     * @return 社区数量对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    @MapKey("name")
    LinkedList<Map<String, Object>> countForGroupByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过社区分包人编号统计
     *
     * @param subcontractorId
     *            社区分包人编号
     * @return 社区居民数量
     * @throws DataAccessException
     *             数据库操作异常
     */
    Long countBySubcontractorId(Serializable subcontractorId) throws DataAccessException;

    /**
     * 通过编号查询联系方式
     *
     * @param id
     *            需要查询的对象编号
     * @return 查询到的对象
     */
    T selectCorrelationAndPhoneNumbersById(Serializable id);
}
