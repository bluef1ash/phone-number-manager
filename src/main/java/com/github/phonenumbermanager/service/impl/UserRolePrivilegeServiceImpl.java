package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.entity.UserRolePrivilege;
import com.github.phonenumbermanager.mapper.UserRolePrivilegeMapper;
import com.github.phonenumbermanager.service.UserRolePrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userRolePrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<UserRolePrivilegeMapper, UserRolePrivilege> implements UserRolePrivilegeService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(UserRolePrivilege userRolePrivilege) {
        return userRolePrivilegeMapper.deleteByUserRolePrivilege(userRolePrivilege) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(UserRole userRole, Long[] privilegeIds) {
        List<UserRolePrivilege> userRolePrivileges = new ArrayList<>();
        UserRolePrivilege userRolePrivilege;
        for (Long privilegeId : privilegeIds) {
            if (privilegeId != 0) {
                userRolePrivilege = new UserRolePrivilege();
                userRolePrivilege.setRoleId(userRole.getId());
                userRolePrivilege.setPrivilegeId(privilegeId);
                userRolePrivileges.add(userRolePrivilege);
            }
        }
        return saveBatch(userRolePrivileges);
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }
}
