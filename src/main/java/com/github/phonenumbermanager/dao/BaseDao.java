package com.github.phonenumbermanager.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 基础DAO接口
 *
 * @param <T> 实体对象类
 * @author 廿二月的天
 */
public interface BaseDao<T> {
    /**
     * 插入对象到数据库
     *
     * @param obj 实体对象
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    long insert(T obj) throws DataAccessException;

    /**
     * 用主键编号删除对象
     *
     * @param id 主键编号
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    long deleteById(Serializable id) throws DataAccessException;

    /**
     * 通过名称删除对象
     *
     * @param name 实体名称
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    long deleteByName(String name) throws DataAccessException;

    /**
     * 更新对象
     *
     * @param obj 实体对象
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    long update(T obj) throws DataAccessException;

    /**
     * 查询所有对象
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectAll() throws DataAccessException;

    /**
     * 通过主键编号查询对象
     *
     * @param id 主键编号
     * @return 实体对象
     * @throws DataAccessException 数据库操作异常
     */
    T selectById(Serializable id) throws DataAccessException;

    /**
     * 通过名称查询对象
     *
     * @param name 实体名称
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByName(String name) throws DataAccessException;

    /**
     * 通过实体对象查询对象
     *
     * @param object 实体对象
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByObject(T object) throws DataAccessException;

    /**
     * 截断表
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    long truncateTable() throws DataAccessException;

    /**
     * 查询对象的编号和名称
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectForIdAndName() throws DataAccessException;

    /**
     * 批量插入数据
     *
     * @param objects 对象集合
     * @return 插入行数
     * @throws DataAccessException 数据库操作异常
     */
    long insertBatch(@Param("objects") List<T> objects) throws DataAccessException;

    /**
     * 通过系统用户数据查询
     *
     * @param userData          系统用户数据
     * @param communityCompanyType   社区用户编号
     * @param subdistrictCompanyType 街道用户编号
     * @return 社区楼长集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByUserData(@Param("userData") List<Map<String, Object>> userData, @Param("communityCompanyType") Serializable communityCompanyType, @Param("subdistrictCompanyType") Serializable subdistrictCompanyType) throws DataAccessException;

    /**
     * 通过编号查询对象与所属社区
     *
     * @param id 对象编号
     * @return 对象与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    T selectAndCommunityById(Serializable id) throws DataAccessException;

    /**
     * 通过所属街道编号删除
     *
     * @param subdistrictId 需要删除的所属街道编号
     * @throws DataAccessException 数据库操作异常
     */
    void deleteBySubdistrictId(Serializable subdistrictId) throws DataAccessException;

    /**
     * 通过社区编号查询
     *
     * @param communityId 社区编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectAndCommunityByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 查询所有与所属社区
     *
     * @param communityIds 多个社区编号
     * @return 所有与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectAndCommunityByCommunityIds(@Param("communityIds") List<Serializable> communityIds) throws DataAccessException;


    /**
     * 通过用户角色查询对象
     *
     * @param companyType            系统用户角色类别
     * @param companyId              系统用户角色定位编号
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param object                 需要查询的对象
     * @return 对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByUserRole(@Param("companyType") Serializable companyType, @Param("companyId") Serializable companyId, @Param("systemCompanyType") Serializable systemCompanyType, @Param("communityCompanyType") Serializable communityCompanyType, @Param("subdistrictCompanyType") Serializable subdistrictCompanyType, @Param("object") T object) throws DataAccessException;

    /**
     * 通过社区编号查询对象的数量
     *
     * @param communityId 社区编号
     * @return 对象的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过姓名+地址查询姓名与社区编号
     *
     * @param nameAddress   姓名与家庭住址
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByNameAndAddress(@Param("nameAddress") String nameAddress, @Param("id") Serializable id, @Param("subdistrictId") Serializable subdistrictId) throws DataAccessException;

    /**
     * 通过电话数组查询姓名与社区编号
     *
     * @param phones        多个联系方式
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByPhones(@Param("phones") List<String> phones, @Param("id") Serializable id, @Param("subdistrictId") Serializable subdistrictId) throws DataAccessException;

    /**
     * 统计所有社区居民数量
     *
     * @return 所有社区居民数量
     * @throws DataAccessException 数据库操作异常
     */
    Long count() throws DataAccessException;

    /**
     * 街道分组统计数量
     *
     * @return 社区数量对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> countForGroupSubdistrict() throws DataAccessException;

    /**
     * 通过街道单位编号社区分组统计数量
     *
     * @param subdistrictId 街道编号
     * @return 社区数量对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> countForGroupCommunity(Serializable subdistrictId) throws DataAccessException;

    /**
     * 统计社区
     *
     * @param communityId 社区编号
     * @return 社区数量对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> countForGroupByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过社区分包人编号统计
     *
     * @param subcontractorId 社区分包人编号
     * @return 社区居民数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countBySubcontractorId(Serializable subcontractorId) throws DataAccessException;
}
