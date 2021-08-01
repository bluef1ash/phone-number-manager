package com.github.phonenumbermanager.mapper;

import com.github.phonenumbermanager.entity.UserRoleRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与角色中间DAO接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface UserRoleRelationMapper extends CommonMapper<UserRoleRelation> {
}
