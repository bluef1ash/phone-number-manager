package la.isx.phone_number_manager.main.dao;

import java.util.List;
/**
 * 基础DAO接口
 * @param <T> 实体对象类
 */
public interface BaseDao<T> {
	/**
	 * 插入对象到数据库
	 * @param obj 实体对象
	 * @return 影响的行数
	 * @throws Exception 操作异常
	 */
    public int insertObject(T obj) throws Exception;
    /**
     * 用主键ID删除对象
     * @param id 主键ID
     * @return 影响的行数
     * @throws Exception
     */
    public int deleteObjectById(Integer id) throws Exception;
    /**
     * 通过名称删除对象
     * @param name 实体名称
     * @return 影响的行数
     * @throws Exception 操作异常
     */
    public int deleteObjectsByName(String name) throws Exception;
    /**
     * 更新对象
     * @param obj 实体对象
     * @return 影响的行数
     * @throws Exception 操作异常
     */
    public int updateObject(T obj) throws Exception;
    /**
     * 查询所有对象
     * @return 实体对象集合
     * @throws Exception
     */
    public List<T> selectObjectsAll() throws Exception;
    /**
     * 通过主键ID查询对象
     * @param id 主键ID
     * @return 实体对象
     * @throws Exception
     */
    public T selectObjectById(Integer id) throws Exception;
    /**
     * 通过名称查询对象
     * @param name 实体名称
     * @return 实体对象集合
     * @throws Exception 操作异常
     */
    public List<T> selectObjectsByName(String name) throws Exception;
    /**
     * 通过实体对象查询对象
     * @param object 实体对象
     * @return 实体对象集合
     * @throws Exception 操作异常
     */
	public List<T> selectObjectsByObject(T object) throws Exception;
}