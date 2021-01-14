package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户角色与用户权限中间实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class UserRolePrivilege implements Serializable {
    private Long roleId;
    private Long privilegeId;
    private Timestamp createTime;
    private Timestamp updateTime;
}
