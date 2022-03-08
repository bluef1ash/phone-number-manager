package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.SystemUserCompany;
import com.github.phonenumbermanager.mapper.SystemUserCompanyMapper;
import com.github.phonenumbermanager.service.SystemUserCompanyService;

/**
 * 系统用户与单位中间业务实现
 *
 * @author 廿二月的天
 */
@Service
public class SystemUserCompanyServiceImpl extends BaseServiceImpl<SystemUserCompanyMapper, SystemUserCompany>
    implements SystemUserCompanyService {}
