package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.UserRoleRelation;
import com.github.phonenumbermanager.mapper.UserRoleRelationMapper;
import com.github.phonenumbermanager.service.SystemUserRoleService;

/**
 * 系统用户与角色中间业务实现
 *
 * @author 廿二月的天
 */
@Service("SystemUserRoleService")
public class SystemUserRoleServiceImpl extends BaseServiceImpl<UserRoleRelationMapper, UserRoleRelation>
    implements SystemUserRoleService {}
