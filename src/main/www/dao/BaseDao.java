package www.dao;

import org.springframework.dao.DataAccessException;

import java.util.List;

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
    int insertObject(T obj) throws DataAccessException;

    /**
     * 用主键ID删除对象
     *
     * @param id 主键ID
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteObjectById(Long id) throws DataAccessException;

    /**
     * 通过名称删除对象
     *
     * @param name 实体名称
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteObjectsByName(String name) throws DataAccessException;

    /**
     * 更新对象
     *
     * @param obj 实体对象
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    int updateObject(T obj) throws DataAccessException;

    /**
     * 查询所有对象
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsAll() throws DataAccessException;

    /**
     * 通过主键编号查询对象
     *
     * @param id 主键编号
     * @return 实体对象
     * @throws DataAccessException 数据库操作异常
     */
    T selectObjectById(Long id) throws DataAccessException;

    /**
     * 通过名称查询对象
     *
     * @param name 实体名称
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsByName(String name) throws DataAccessException;

    /**
     * 通过实体对象查询对象
     *
     * @param object 实体对象
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsByObject(T object) throws DataAccessException;

    /**
     * 截断表
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    int truncateTable() throws DataAccessException;

    /**
     * 查询对象的编号和名称
     *
     * @return 实体对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsForIdAndName() throws DataAccessException;
}
