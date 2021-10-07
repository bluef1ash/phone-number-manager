package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import com.github.phonenumbermanager.mapper.RolePrivilegeRelationMapper;
import com.github.phonenumbermanager.service.UserRolePrivilegeService;

/**
 * 系统用户角色权限中间业务实现
 *
 * @author 廿二月的天
 */
@Service("userRolePrivilegeService")
public class UserRolePrivilegeServiceImpl extends BaseServiceImpl<RolePrivilegeRelationMapper, RolePrivilegeRelation>
    implements UserRolePrivilegeService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long roleId, Long privilegeId) {
        QueryWrapper<RolePrivilegeRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("privilege_id", privilegeId);
        wrapper.eq("role_id", roleId);
        return baseMapper.delete(wrapper) > 0;
    }
}
