package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.DormitoryManagerPhoneNumber;
import com.github.phonenumbermanager.mapper.DormitoryManagerPhoneNumberMapper;
import com.github.phonenumbermanager.service.DormitoryManagerPhoneNumberService;

/**
 * 社区楼片长信息与联系方式中间业务接口实现
 *
 * @author 廿二月的天
 */
@Service
public class DormitoryManagerPhoneNumberServiceImpl
    extends BaseServiceImpl<DormitoryManagerPhoneNumberMapper, DormitoryManagerPhoneNumber>
    implements DormitoryManagerPhoneNumberService {}
