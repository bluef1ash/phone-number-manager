package com.github.phonenumbermanager.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.entity.Subcontractor;

/**
 * 社区分包人员Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface SubcontractorMapper extends BaseMapper<Subcontractor> {
    /**
     * 查询所有社区分包人员与所属联系方式
     *
     * @return 所有社区分包人员与所属联系方式
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<Subcontractor> selectAndPhoneNumber() throws DataAccessException;

    /**
     * 通过单位编号关联分组统计社区居民与社区居民楼片长
     *
     * @param companyIds
     *            单位编号数组
     * @return 分组统计社区居民与社区居民楼片长
     * @throws DataAccessException
     *             数据库操作异常
     */
    @MapKey("subcontractor_name")
    List<Map<String, Object>> selectCorrelationCountByCompanyIds(Long[] companyIds) throws DataAccessException;
}
