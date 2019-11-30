package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.entity.UserRolePrivilege;
import com.github.phonenumbermanager.service.UserRolePrivilegeService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userRolePrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<UserRolePrivilege> implements UserRolePrivilegeService {

    @Override
    public long delete(UserRolePrivilege userRolePrivilege) {
        return userRolePrivilegeDao.deleteByUserRolePrivilege(userRolePrivilege);
    }

    @Override
    public long create(UserRole userRole, Long[] privilegeIds) {
        List<UserRolePrivilege> userRolePrivileges = new ArrayList<>();
        UserRolePrivilege userRolePrivilege;
        for (Long privilegeId : privilegeIds) {
            if (privilegeId != 0) {
                userRolePrivilege = new UserRolePrivilege();
                userRolePrivilege.setRoleId(userRole.getId());
                userRolePrivilege.setPrivilegeId(privilegeId);
                userRolePrivilege.setCreateTime(DateUtils.getTimestamp(new Date()));
                userRolePrivilege.setUpdateTime(DateUtils.getTimestamp(new Date()));
                userRolePrivileges.add(userRolePrivilege);
            }
        }
        return userRolePrivilegeDao.insertBatch(userRolePrivileges);
    }

    @Override
    public long update(UserRolePrivilege userRolePrivilege) {
        userRolePrivilege.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(userRolePrivilege);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }
}
