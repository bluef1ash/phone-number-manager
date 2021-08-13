package com.github.phonenumbermanager.mapper;

import java.io.Serializable;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;

/**
 * 联系方式Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface PhoneNumberMapper extends CommonMapper<PhoneNumber> {
    /**
     * 通过来源类型与来源编号查询
     *
     * @param sourceType
     *            来源类型
     * @param sourceId
     *            来源编号
     * @return 查询到的联系方式对象
     * @throws DataAccessException
     *             数据库操作异常
     */
    PhoneNumber selectBySourceTypeAndSourceId(PhoneNumberSourceTypeEnum sourceType, Serializable sourceId)
        throws DataAccessException;
}
