package www.dao;

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
    int insertObject(T obj) throws DataAccessException;

    /**
     * 用主键编号删除对象
     *
     * @param id 主键编号
     * @return 影响的行数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteObjectById(Serializable id) throws DataAccessException;

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
    T selectObjectById(Serializable id) throws DataAccessException;

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

    /**
     * 批量插入数据
     *
     * @param objects 对象集合
     * @return 插入行数
     * @throws DataAccessException 数据库操作异常
     */
    int insertBatch(@Param("objects") List<T> objects) throws DataAccessException;

    /**
     * 通过系统用户数据查询
     *
     * @param userData          系统用户数据
     * @param communityRoleId   社区用户编号
     * @param subdistrictRoleId 街道用户编号
     * @return 社区楼长集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByUserData(@Param("userData") List<Map<String, Object>> userData, @Param("communityRoleId") Long communityRoleId, @Param("subdistrictRoleId") Long subdistrictRoleId) throws DataAccessException;

    /**
     * 通过编号查询对象与所属社区
     *
     * @param id 对象编号
     * @return 对象与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    T selectObjectAndCommunityById(Serializable id) throws DataAccessException;

    /**
     * 通过所属街道编号删除
     *
     * @param subdistrictId 需要删除的所属街道编号
     * @throws DataAccessException 数据库操作异常
     */
    void deleteBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 通过社区编号查询
     *
     * @param communityId 社区编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsAndCommunityByCommunityId(Long communityId) throws DataAccessException;

    /**
     * 查询所有与所属社区
     *
     * @param communityIds 多个社区编号
     * @return 所有与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectObjectsAndCommunityByCommunityIds(@Param("communityIds") List<Long> communityIds) throws DataAccessException;


    /**
     * 通过用户角色查询对象
     *
     * @param userRoleId         系统用户角色类别编号
     * @param userRoleLocationId 系统用户角色定位编号
     * @param systemRoleId       系统用户级角色编号
     * @param communityRoleId    社区级角色编号
     * @param subdistrictRoleId  街道级角色编号
     * @param object             需要查询的对象
     * @return 对象集合
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByUserRole(@Param("userRoleId") Long userRoleId, @Param("userRoleLocationId") Long userRoleLocationId, @Param("systemRoleId") Long systemRoleId, @Param("communityRoleId") Long communityRoleId, @Param("subdistrictRoleId") Long subdistrictRoleId, @Param("object") T object) throws DataAccessException;

    /**
     * 通过社区编号查询对象的数量
     *
     * @param communityId 社区编号
     * @return 对象的数量
     * @throws DataAccessException 数据库操作异常
     */
    Long countObjectByCommunityId(Long communityId) throws DataAccessException;

    /**
     * 通过姓名+地址查询姓名与社区编号
     *
     * @param nameAddress   姓名与家庭住址
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByNameAndAddress(@Param("nameAddress") String nameAddress, @Param("id") Serializable id, @Param("subdistrictId") Long subdistrictId) throws DataAccessException;

    /**
     * 通过电话数组查询姓名与社区编号
     *
     * @param phones        多个联系方式
     * @param id            编号
     * @param subdistrictId 街道办事处编号
     * @return 查询到的对象
     * @throws DataAccessException 数据库操作异常
     */
    List<T> selectByPhones(@Param("phones") List<String> phones, @Param("id") Serializable id, @Param("subdistrictId") Long subdistrictId) throws DataAccessException;

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
    LinkedList<Map<String, Object>> countForGroupCommunity(Long subdistrictId) throws DataAccessException;

    /**
     * 统计社区
     *
     * @param communityId 社区编号
     * @return 社区数量对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> countForGroupByCommunityId(Long communityId) throws DataAccessException;
}
