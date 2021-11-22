package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

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
    implements PhoneNumberService {}
