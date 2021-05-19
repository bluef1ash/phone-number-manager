package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;

import java.io.Serializable;

/**
 * 联系方式Service层
 *
 * @author 廿二月的天
 */
public interface PhoneNumberService extends BaseService<PhoneNumber> {
    /**
     * 通过来源删除
     *
     * @param sourceType 来源类型
     * @param sourceId   来源编号
     * @return 是否删除成功
     */
    boolean removeBySource(PhoneNumberSourceTypeEnum sourceType, Serializable sourceId);
}
