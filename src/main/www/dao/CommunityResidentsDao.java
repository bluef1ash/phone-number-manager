package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.CommunityResident;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社区居民DAO接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentsDao extends BaseDao<CommunityResident> {
    /**
     * 通过社区居民主键ID查询社区居民与所属社区
     *
     * @param id 社区居民编号
     * @return 社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    CommunityResident selectCommunityResidentAndCommunityById(Integer id) throws DataAccessException;

    /**
     * 通过社区居民查询社区居民与所属社区
     *
     * @param communityResident 社区居民
     * @return 多个社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 批量插入社区居民数据
     *
     * @param residents 多个社区居民
     * @return 插入行数
     * @throws DataAccessException 数据库操作异常
     */
    int insertBatchCommunityResidents(List<CommunityResident> residents) throws DataAccessException;

    /**
     * 查询所有社区居民与所属社区
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsAndCommunityByCommunityIds(@Param("communityIds") List<Integer> communityIds) throws DataAccessException;

    /**
     * 查询所有社区居民与所属社区和街道
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(@Param("communityIds") List<Integer> communityIds) throws DataAccessException;

    /**
     * 通过姓名+地址查询姓名与社区编号
     *
     * @param nameAddress         社区居民姓名与家庭住址
     * @param communityResidentId 社区居民编号
     * @return 查询到的社区居民
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentByNameAndAddress(@Param("nameAddress") String nameAddress, @Param("communityResidentId") Integer communityResidentId) throws DataAccessException;

    /**
     * 通过电话数组查询姓名与社区编号
     *
     * @param residentPhones      多个联系方式
     * @param communityResidentId 社区居民编号
     * @return 查询到的社区居民
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentByPhones(@Param("residentPhones") List<String> residentPhones, @Param("communityResidentId") Integer communityResidentId) throws DataAccessException;

    /**
     * 通过社区编号查询所属社区居民
     *
     * @param roleLocationId 角色定位编号
     * @return 查询到的社区居民
     * @throws DataAccessException 数据库操作异常
     */
    List<CommunityResident> selectCommunityResidentsAndCommunityByCommunityId(Integer roleLocationId) throws DataAccessException;

    /**
     * 通过社区居民查询社区居民与所属社区的数量
     *
     * @param communityResident 社区居民
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Integer countCommunityResidentsByCommunityResident(@Param("communityResident") CommunityResident communityResident) throws DataAccessException;

    /**
     * 通过多个社区编号查询所有社区居民与所属社区的数量
     *
     * @param communityIds 多个社区编号
     * @return 所有社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Integer countCommunityResidentsAndCommunityByCommunityIds(@Param("communityIds") List<Integer> communityIds) throws DataAccessException;

    /**
     * 通过社区编号查询社区居民与所属社区的数量
     *
     * @param communityId 社区编号
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Integer countCommunityResidentsByCommunityId(Integer communityId) throws DataAccessException;

    /**
     * 通过街道编号查询社区居民与所属社区的数量
     *
     * @param subdistrictId 街道编号
     * @return 社区居民与所属社区的数量
     * @throws DataAccessException 数据库操作异常
     */
    Integer countCommunityResidentsBySubdistrictId(Integer subdistrictId) throws DataAccessException;

    /**
     * 统计所有社区居民数量
     *
     * @return 所有社区居民数量
     * @throws DataAccessException 数据库操作异常
     */
    Integer countCommunityResidents() throws DataAccessException;

    /**
     * 通过所属街道编号删除
     *
     * @param subdistrictId 需要删除的所属街道编号
     * @throws DataAccessException 数据库操作异常
     */
    void deleteCommunityResidentsBySubdistrictId(Integer subdistrictId) throws DataAccessException;
}
