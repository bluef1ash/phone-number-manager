package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.Community;

import java.util.List;

/**
 * 社区DAO接口
 *
 * @author 廿二月的天
 */
public interface CommunitiesDao extends BaseDao<Community> {
    /**
     * 通过主键ID查询社区和所属街道
     *
     * @param communityId 社区编号
     * @return 一个社区信息和所属街道信息
     * @throws DataAccessException 数据库操作异常
     */
    Community selectCommunityAndSubdistrictById(Long communityId) throws DataAccessException;

    /**
     * 查询所有社区和所属街道
     *
     * @return 全部查询的社区和所属街道的信息
     * @throws DataAccessException 数据库操作异常
     */
    List<Community> selectCommunitiesAndSubdistrictAll() throws DataAccessException;

    /**
     * 通过所属街道编号关联查询社区
     *
     * @param subdistrictId 所属街道编号
     * @return 所属街道的所有社区
     * @throws DataAccessException 数据库操作异常
     */
    List<Community> selectCommunitiesCorrelationBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 通过所属街道编号查询社区
     *
     * @param subdistrictId 所属街道编号
     * @return 所属街道的所有社区
     * @throws DataAccessException 数据库操作异常
     */
    List<Community> selectCommunitiesBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 通过社区名称查询社区编号
     *
     * @param communityAliasName 社区名称
     * @return 社区编号
     * @throws DataAccessException 数据库操作异常
     */
    Long selectCommunityIdByCommunityName(String communityAliasName) throws DataAccessException;

    /**
     * 通过所属街道名称查询社区
     *
     * @param subdistrictName 所属街道名称
     * @return 所属街道的所有社区
     * @throws DataAccessException 数据库操作异常
     */
    List<Community> selectCommunitiesBySubdistrictName(String subdistrictName) throws DataAccessException;

    /**
     * 通过社区编号查询指标
     *
     * @param communityId 社区编号
     * @return 社区指标
     * @throws DataAccessException 数据库操作异常
     */
    Integer selectActualNumberByCommunityId(Long communityId) throws DataAccessException;

    /**
     * 通过所属街道编号求和指标
     *
     * @param subdistrictId 所属街道编号
     * @return 指标的和
     * @throws DataAccessException 数据库操作异常
     */
    Integer sumActualNumberBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 求和所有社区指标
     *
     * @return 所有社区指标之和
     * @throws DataAccessException 数据库操作异常
     */
    Long sumActualNumber() throws DataAccessException;

    /**
     * 通过所属街道编号统计各个社区
     *
     * @param subdistrictId 所属街道编号
     * @return 统计社区个数
     * @throws DataAccessException 数据库操作异常
     */
    List<Community> countCommunitiesBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 关联查询所有社区与街道
     *
     * @return 社区与街道对象集合
     * @throws DataAccessException 数据库连接异常
     */
    List<Community> selectCommunitiesCorrelationSubdistrictsAll() throws DataAccessException;

    /**
     * 通过社区编号关联查询
     *
     * @param communityId 社区编号
     * @return 查询到的关联社区对象
     * @throws DataAccessException 数据库连接异常
     */
    Community selectCommunityCorrelationById(Long communityId) throws DataAccessException;
}
