package com.github.phonenumbermanager.service;

import java.util.List;

import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.PhoneNumber;

/**
 * 社区居民业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过联系方式集合关联查找
     *
     * @param phoneNumbers
     *            联系方式集合
     * @return 查找到的数据
     */
    List<CommunityResident> listByPhoneNumbers(List<PhoneNumber> phoneNumbers);
}
