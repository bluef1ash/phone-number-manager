package www.service;

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
    long createObject(T obj) throws Exception;

    /**
     * 删除对象到DAO层
     *
     * @param id 对象实体编号
     * @return 删除的行数
     * @throws Exception SERVICE层异常
     */
    int deleteObjectById(Serializable id) throws Exception;

    /**
     * 通过名称删除对象到DAO层
     *
     * @param name 对象实体名称
     * @return 删除的行数
     * @throws Exception SERVICE层异常
     */
    int deleteObjectsByName(String name) throws Exception;

    /**
     * 更新对象到DAO层
     *
     * @param obj 需要更新的对象
     * @return 更新的行数
     * @throws Exception SERVICE层异常
     */
    int updateObject(T obj) throws Exception;

    /**
     * 查找所有对象（不含分页）
     *
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> findObjects() throws Exception;

    /**
     * 查找所有对象（含分页）
     *
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findObjects(Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过主键编号查找
     *
     * @param id 主键编号
     * @return 查找的对象
     * @throws Exception SERVICE层异常
     */
    T findObject(Serializable id) throws Exception;

    /**
     * 通过name查找（含分页）
     *
     * @param name         查找的对象名称
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findObjects(String name, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过对象查找（含分页）
     *
     * @param object       查找的对象
     * @param pageNumber   分页页码
     * @param pageDataSize 每页展示数量
     * @return 查找的对象集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findObjects(T object, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 查找所有编号和名称
     *
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> findObjectsForIdAndName() throws Exception;

    /**
     * 通过对象查找
     *
     * @param object 对象名称
     * @return 查找的对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> findObjects(T object) throws Exception;

    /**
     * 通过姓名与地址查找
     *
     * @param nameAddress   姓名与地址
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 对象集合
     * @throws Exception SERVICE层异常
     */
    List<T> findByNameAndAddress(String nameAddress, Serializable id, Long subdistrictId) throws Exception;

    /**
     * 通过联系方式与地址查找所属社区
     *
     * @param phones        联系方式集合
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查找到的对象
     * @throws Exception SERVICE层异常
     */
    List<T> findByPhones(List<String> phones, Serializable id, Long subdistrictId) throws Exception;
}
