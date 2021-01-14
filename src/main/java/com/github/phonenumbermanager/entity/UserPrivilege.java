package com.github.phonenumbermanager.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * 用户权限实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class UserPrivilege implements Serializable {
    private Long id;
    private String name;
    private String constraintAuth;
    private String uri;
    private Long parentId;
    private String iconName;
    private Integer orders = 0;
    private Boolean display;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer level;
    private List<UserRole> userRoles;
    private Set<UserPrivilege> subUserPrivileges;
}
