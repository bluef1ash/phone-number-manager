package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.CompanyPermission;
import com.github.phonenumbermanager.mapper.CompanyPermissionMapper;
import com.github.phonenumbermanager.service.CompanyPermissionService;

/**
 * 单位与系统权限中间业务接口实现
 *
 * @author 廿二月的天
 */
@Service("companyPermissionService")
public class CompanyPermissionServiceImpl extends BaseServiceImpl<CompanyPermissionMapper, CompanyPermission>
    implements CompanyPermissionService {}
