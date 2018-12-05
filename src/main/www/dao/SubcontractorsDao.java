package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.Subcontractor;

import java.util.List;

/**
 * 社区分包人DAO接口
 *
 * @author 廿二月的天
 */
public interface SubcontractorsDao extends BaseDao<Subcontractor> {
    /**
     * 通过所属社区编号编号查询
     *
     * @param communityId 所属社区编号
     * @return 社区分包人对象集合
     * @throws DataAccessException 数据库连接异常
     */
    List<Subcontractor> selectSubcontractors(Long communityId) throws DataAccessException;

    /**
     * 通过所属街道编号查询
     *
     * @param subdistrictId 所属街道编号
     * @return 社区分包人对象集合
     * @throws DataAccessException 数据库连接异常
     */
    List<Subcontractor> selectSubcontractorsBySubdistrictId(Long subdistrictId) throws DataAccessException;

    /**
     * 关联查询所有
     *
     * @return 社区分包人对象集合
     * @throws DataAccessException 数据库连接异常
     */
    List<Subcontractor> selectSubcontractorsAll() throws DataAccessException;
}
