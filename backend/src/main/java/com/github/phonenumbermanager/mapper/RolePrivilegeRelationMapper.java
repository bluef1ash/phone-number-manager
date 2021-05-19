package com.github.phonenumbermanager.mapper;

import com.github.phonenumbermanager.entity.RolePrivilegeRelation;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * 系统用户角色与权限中间DAO接口
 *
 * @author 廿二月的天
 */
@Repository
public interface RolePrivilegeRelationMapper extends CommonMapper<RolePrivilegeRelation> {

    /**
     * 通过中间表对象删除
     *
     * @param rolePrivilegeRelation 系统用户
     * @return 删除成功记录数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteByRolePrivilegeRelation(RolePrivilegeRelation rolePrivilegeRelation) throws DataAccessException;
}
