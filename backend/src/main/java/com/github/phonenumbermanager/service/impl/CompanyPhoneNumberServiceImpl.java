package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.CompanyPhoneNumber;
import com.github.phonenumbermanager.mapper.CompanyPhoneNumberMapper;
import com.github.phonenumbermanager.service.CompanyPhoneNumberService;

/**
 * 单位与联系方式中间业务接口实现
 *
 * @author 廿二月的天
 */
@Service("companyPhoneNumberService")
public class CompanyPhoneNumberServiceImpl extends BaseServiceImpl<CompanyPhoneNumberMapper, CompanyPhoneNumber>
    implements CompanyPhoneNumberService {}
