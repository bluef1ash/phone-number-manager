package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.RolePrivilegeRelationMapper;
import com.github.phonenumbermanager.service.UserRolePrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userRolePrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<RolePrivilegeRelationMapper, RolePrivilegeRelation> implements UserRolePrivilegeService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(RolePrivilegeRelation rolePrivilegeRelation) {
        return rolePrivilegeRelationMapper.deleteByRolePrivilegeRelation(rolePrivilegeRelation) > 0;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }
}
