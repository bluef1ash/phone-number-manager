package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 用户角色实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class UserRole implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long parentId;
    private Set<SystemUser> systemUsers;
    private Set<UserPrivilege> userPrivileges;
}
