package com.github.phonenumbermanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.entity.Subcontractor;

/**
 * 社区分包人员 Mapper 接口
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
}
