package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.SubcontractorPhoneNumber;
import com.github.phonenumbermanager.mapper.SubcontractorPhoneNumberMapper;
import com.github.phonenumbermanager.service.SubcontractorPhoneNumberService;

/**
 * 社区分包人员与联系方式中间业务接口实现
 *
 * @author 廿二月的天
 */
@Service
public class SubcontractorPhoneNumberServiceImpl
    extends BaseServiceImpl<SubcontractorPhoneNumberMapper, SubcontractorPhoneNumber>
    implements SubcontractorPhoneNumberService {}
