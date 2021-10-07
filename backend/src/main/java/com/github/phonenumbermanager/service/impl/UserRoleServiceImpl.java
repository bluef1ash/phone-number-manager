package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.entity.UserRole;
import com.github.phonenumbermanager.entity.UserRoleRelation;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.RolePrivilegeRelationMapper;
import com.github.phonenumbermanager.mapper.UserRoleMapper;
import com.github.phonenumbermanager.mapper.UserRoleRelationMapper;
import com.github.phonenumbermanager.service.UserRoleService;

/**
 * 系统用户角色业务实现
 *
 * @author 廿二月的天
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    @Resource
    private UserRoleRelationMapper userRoleRelationMapper;
    @Resource
    private RolePrivilegeRelationMapper rolePrivilegeRelationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Serializable id) {
        QueryWrapper<UserRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        if (userRoleRelationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("不允许删除存在有下属系统用户的系统用户角色！");
        }
        QueryWrapper<RolePrivilegeRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", id);
        return baseMapper.deleteById(id) > 0 && rolePrivilegeRelationMapper.delete(queryWrapper) > 0;
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
