package main.service;

import java.util.List;
import java.util.Map;

/**
 * Service层基本接口
 *
 * @param <T> DAO接口泛型
 */
public interface BaseService<T> {
    /**
     * 新建对象到DAO层
     *
     * @param obj 对象实体
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public int createObject(T obj) throws Exception;

    /**
     * 删除对象到DAO层
     *
     * @param id 对象实体编号
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public int deleteObjectById(Integer id) throws Exception;

    /**
     * 通过名称删除对象到DAO层
     *
     * @param name 对象实体名称
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public int deleteObjectsByName(String name) throws Exception;

    /**
     * 更新对象到DAO层
     *
     * @param obj 需要更新的对象
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public int updateObject(T obj) throws Exception;

    /**
     * 查找所有对象（不含分页）
     *
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public List<T> findObjects() throws Exception;

    /**
     * 查找所有对象（含分页）
     *
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public Map<String, Object> findObjects(Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过主键ID查找
     *
     * @param id 主键编号
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public T findObject(Integer id) throws Exception;

    /**
     * 通过name查找
     *
     * @param name 对象名称
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public Map<String, Object> findObjects(String name, Integer pageNum, Integer pageSize) throws Exception;

    /**
     * 通过对象查找
     *
     * @param object 对象名称
     * @return 从DAO层返回
     * @throws Exception DAO异常
     */
    public Map<String, Object> findObjects(T object, Integer pageNum, Integer pageSize) throws Exception;
}
