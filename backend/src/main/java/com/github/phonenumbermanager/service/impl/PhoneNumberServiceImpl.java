package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.mapper.PhoneNumberMapper;
import com.github.phonenumbermanager.service.PhoneNumberService;

/**
 * 联系方式业务实现
 *
 * @author 廿二月的天
 */
@Service("phoneNumberService")
public class PhoneNumberServiceImpl extends BaseServiceImpl<PhoneNumberMapper, PhoneNumber>
    implements PhoneNumberService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeBySource(PhoneNumberSourceTypeEnum sourceType, Serializable sourceId) {
        Map<String, Object> columnMap = new HashMap<>(2);
        columnMap.put("sourceType", sourceType);
        columnMap.put("sourceId", sourceId);
        return baseMapper.deleteByMap(columnMap) > 0;
    }
}
