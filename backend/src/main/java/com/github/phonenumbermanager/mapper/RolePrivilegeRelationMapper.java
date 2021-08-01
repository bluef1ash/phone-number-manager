package com.github.phonenumbermanager.mapper;

import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户角色与权限中间DAO接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface RolePrivilegeRelationMapper extends CommonMapper<RolePrivilegeRelation> {
}
