package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.mapper.UserRoleMapper;
import com.github.phonenumbermanager.service.UserRoleService;

/**
 * 系统用户角色业务实现
 *
 * @author 廿二月的天
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    @Resource
    private SystemUserMapper systemUserMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        Long systemUserCount = systemUserMapper.countByRoleId(id);
        if (systemUserCount == null || systemUserCount == 0) {
            throw new BusinessException("不允许删除存在有下属系统用户的系统用户角色！");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public IPage<UserRole> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        Page<UserRole> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectAndSystemUsers(page);
    }

    @Override
    public IPage<UserRole> getAndPrivileges(Integer pageNumber, Integer pageDataSize) {
        Page<UserRole> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectAndPrivileges(page);
    }

    @Override
    public UserRole getCorrelation(Serializable id) {
        return baseMapper.selectAndPrivilegesById(id);
    }
}
