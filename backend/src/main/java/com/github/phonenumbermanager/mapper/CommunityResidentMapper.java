package com.github.phonenumbermanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.PhoneNumber;

/**
 * 社区居民Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface CommunityResidentMapper extends BaseMapper<CommunityResident> {
    /**
     * 通过联系方式集合关联查询
     *
     * @param phoneNumbers
     *            联系方式集合
     * @return 查询到的数据集合
     */
    List<CommunityResident> selectCorrelationByPhoneNumbers(List<PhoneNumber> phoneNumbers);
}
