package com.github.phonenumbermanager.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.github.phonenumbermanager.entity.UserRoleRelation;

/**
 * 用户与角色中间DAO接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface UserRoleRelationMapper extends CommonMapper<UserRoleRelation> {}
