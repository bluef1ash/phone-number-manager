package com.github.phonenumbermanager.service.impl;

import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.UserRoleService;
import com.github.phonenumbermanager.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户角色业务实现
 *
 * @author 廿二月的天
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements UserRoleService {

    @Override
    public long create(UserRole userRole) {
        userRole.setCreateTime(DateUtils.getTimestamp(new Date()));
        userRole.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.create(userRole);
    }

    @Override
    public long update(UserRole userRole) {
        userRole.setUpdateTime(DateUtils.getTimestamp(new Date()));
        return super.update(userRole);
    }

    @Override
    public long delete(Serializable id) {
        Long systemUserCount = systemUserDao.countByRoleId(id);
        if (systemUserCount == null || systemUserCount == 0) {
            throw new BusinessException("不允许删除存在有下属系统用户的系统用户角色！");
        }
        return super.delete(id);
    }

    @Override
    public Map<String, Object> findCorrelation(Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<UserRole> data = userRoleDao.selectAndSystemUsers();
        return find(data);
    }

    @Override
    public Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public Map<String, Object> findAndPrivileges(Integer pageNumber, Integer pageDataSize) {
        setPageHelper(pageNumber, pageDataSize);
        List<UserRole> data = userRoleDao.selectAndPrivileges();
        return find(data);
    }

    @Override
    public UserRole findCorrelation(Serializable id) {
        return userRoleDao.selectAndPrivilegesById(id);
    }
}
