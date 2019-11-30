package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.SystemUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Service层基本接口
 *
 * @param <T> SERVICE接口泛型
 * @author 廿二月的天
 */
public interface BaseService<T> {
    /**
     * 新建对象到DAO层
     *
     * @param obj 对象实体
     * @return 添加的行数
     * @throws Exception SERVICE层异常
     */
    long create(T obj) throws Exception;

    /**
     * 删除对象到DAO层
     *
     * @param id 对象实体编号
     * @return 删除的行数
     * @throws Exception SERVICE层异常
     */
    long delete(Serializable id) throws Exception;

    /**
     * 通过名称删除对象到DAO层
     *
     * @param name 对象实体名称
     * @return 删除的行数
     * @throws Exception SERVICE层异常
     */
    long delete(String name) throws Exception;

    /**
     * 更新对象到DAO层
     *
     * @param obj 需要更新的对象
     * @return 更新的行数
     * @throws Exception SERVICE层异常
     */
    long update(T obj) throws Exception;

    /**
     * 查找所有对象（不含分页）
     *
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> find() throws Exception;

    /**
     * 查找所有对象（含分页）
     *
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过主键编号查找
     *
     * @param id 主键编号
     * @return 查找的对象
     * @throws Exception SERVICE层异常
     */
    T find(Serializable id) throws Exception;

    /**
     * 通过name查找（含分页）
     *
     * @param name         查找的对象名称
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(String name, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过对象查找（含分页）
     *
     * @param object       查找的对象
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(T object, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 查找所有编号和名称
     *
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> findForIdAndName() throws Exception;

    /**
     * 通过对象查找
     *
     * @param object 对象名称
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> find(T object) throws Exception;

    /**
     * 通过姓名与地址查找
     *
     * @param nameAddress   姓名与地址
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> find(String nameAddress, Serializable id, Serializable subdistrictId) throws Exception;

    /**
     * 通过联系方式与地址查找所属社区
     *
     * @param phones        联系方式集合
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查找到的对象
     * @throws Exception SERVICE层异常
     */
    List<T> find(List<String> phones, Serializable id, Serializable subdistrictId) throws Exception;

    /**
     * 查找Excel表头
     *
     * @return 表字段名称
     */
    Map<String, String> findPartStatHead();

    /**
     * 获取录入统计信息
     *
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 统计信息对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(Serializable companyId, Serializable companyType, Serializable systemRoleId, Serializable communityRoleId, Serializable subdistrictRoleId) throws Exception;

    /**
     * 关联查找（包含分页）
     *
     * @param pageNumber   分页页码
     * @param pageDataSize 每页显示的条目数
     * @return 查找到的所有对象与所属对象与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCorrelation(Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 获取柱状图数据
     *
     * @param systemUser             正在登录中的系统用户对象
     * @param companyId              单位编号
     * @param companyType            单位类型
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 柱状图数据
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) throws Exception;
}
