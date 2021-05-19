package com.github.phonenumbermanager.mapper;

import com.github.phonenumbermanager.entity.UserRoleRelation;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * 用户与角色中间DAO接口
 *
 * @author 廿二月的天
 */
@Repository
public interface UserRoleRelationMapper extends CommonMapper<UserRoleRelation> {

    /**
     * 通过中间表对象删除
     *
     * @param userRoleRelation 用户与角色中间对象实体
     * @return 删除成功记录数
     * @throws DataAccessException 数据库操作异常
     */
    int deleteByUserRoleRelation(UserRoleRelation userRoleRelation) throws DataAccessException;
}
