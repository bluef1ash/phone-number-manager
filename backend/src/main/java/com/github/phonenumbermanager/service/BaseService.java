package com.github.phonenumbermanager.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;

/**
 * Service层基本接口
 *
 * @param <T>
 *            SERVICE接口泛型
 * @author 廿二月的天
 */
public interface BaseService<T> extends IService<T> {
    /**
     * 通过对象查找
     *
     * @param object
     *            对象名称
     * @return 查找的对象集合
     */
    List<T> get(T object);

    /**
     * 通过姓名与地址查找
     *
     * @param nameAddress
     *            姓名与地址
     * @param id
     *            编号
     * @param subdistrictId
     *            街道办事处编号
     * @return 对象集合
     */
    List<T> get(String nameAddress, Serializable id, Serializable subdistrictId);

    /**
     * 通过联系方式与地址查找所属社区
     *
     * @param phoneNumbers
     *            联系方式集合
     * @param id
     *            编号
     * @param subdistrictId
     *            街道办事处编号
     * @param sourceType
     *            来源类型
     * @return 查找到的对象
     */
    List<T> get(List<PhoneNumber> phoneNumbers, Serializable id, Serializable subdistrictId,
        PhoneNumberSourceTypeEnum sourceType);

    /**
     * 获取录入统计信息
     *
     * @param companyId
     *            单位编号
     * @param companyType
     *            单位类型
     * @param systemRoleId
     *            系统用户角色编号
     * @param communityRoleId
     *            社区级用户角色编号
     * @param subdistrictRoleId
     *            街道级用户角色编号
     * @return 统计信息对象
     */
    Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemRoleId,
        Serializable communityRoleId, Serializable subdistrictRoleId);

    /**
     * 关联查找
     *
     * @return 查找到的所有对象与所属对象
     */
    List<T> getCorrelation();

    /**
     * 关联查找（包含分页）
     *
     * @param pageNumber
     *            分页页码
     * @param pageDataSize
     *            每页显示的条目数
     * @return 查找到的所有对象与所属对象与分页对象
     */
    IPage<T> getCorrelation(Integer pageNumber, Integer pageDataSize);

    /**
     * 获取柱状图数据
     *
     * @param systemUser
     *            正在登录中的系统用户对象
     * @param companyId
     *            单位编号
     * @param companyType
     *            单位类型
     * @param systemCompanyType
     *            系统单位类型编号
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @return 柱状图数据
     */
    Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType);

    /**
     * 查找所有社区居民或楼长及所属社区
     *
     * @param systemUser
     *            登录的系统用户对象
     * @param phoneNumberSourceTypeEnum
     *            联系方式来源类型
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @param pageNumber
     *            分页页码
     * @param pageDataSize
     *            每页展示的数量
     * @return 查找到的社区居民集合与分页对象
     */
    IPage<T> getCorrelation(SystemUser systemUser, PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum,
        Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber,
        Integer pageDataSize);

    /**
     * 从Excel导入数据
     *
     * @param data
     *            Excel数据
     * @param subdistrictId
     *            导入的街道编号
     * @param configurationsMap
     *            系统配置
     * @return 导入的行数
     */
    boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap);

    /**
     * 通过编号关联查找
     *
     * @param id
     *            需要查找的对象编号
     * @return 对应的对象
     */
    T getCorrelation(Serializable id);

    /**
     * 通过系统用户角色编号与定位角色编号查找社区居民及所属社区
     *
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @param userData
     *            用户数据
     * @return 社区居民与所属社区集合转换的JSON对象
     */
    List<LinkedHashMap<String, Object>> getCorrelation(Serializable communityCompanyType,
        Serializable subdistrictCompanyType, List<Map<String, Object>> userData);

    /**
     * 通过编号关联删除
     *
     * @param id
     *            需要删除的编号
     * @return 删除是否成功
     */
    boolean removeCorrelationById(Serializable id);
}
