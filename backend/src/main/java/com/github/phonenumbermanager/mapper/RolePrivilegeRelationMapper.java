package com.github.phonenumbermanager.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.github.phonenumbermanager.entity.RolePrivilegeRelation;

/**
 * 系统用户角色与权限中间DAO接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface RolePrivilegeRelationMapper extends CommonMapper<RolePrivilegeRelation> {}
