package com.github.phonenumbermanager.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户权限实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_privilege")
public class UserPrivilege implements Serializable {
    private Long id;
    private String name;
    private String constraintAuth;
    private String uri;
    private Long parentId;
    private String iconName;
    private Integer orders;
    @TableField("is_display")
    private Boolean display;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
    @TableField(exist = false)
    private Integer level;
    @TableField(exist = false)
    private List<UserRole> userRoles;
    @TableField(exist = false)
    private Set<UserPrivilege> subUserPrivileges;
}
