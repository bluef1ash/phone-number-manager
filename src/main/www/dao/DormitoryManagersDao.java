package www.dao;

import org.springframework.dao.DataAccessException;
import www.entity.DormitoryManager;

/**
 * 楼片长DAO接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagersDao extends BaseDao<DormitoryManager> {

    /**
     * 通过社区编号查询最后一个编号
     *
     * @param communityId 社区编号
     * @return 楼片长编号
     * @throws DataAccessException 数据库连接异常
     */
    String selectLastIdByCommunityId(Long communityId) throws DataAccessException;
}
