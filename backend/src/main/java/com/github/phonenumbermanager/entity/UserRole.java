package com.github.phonenumbermanager.entity;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户角色对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_role")
@ApiModel("用户角色对象实体")
public class UserRole extends BaseEntity<UserRole> {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<SystemUser> systemUsers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<UserPrivilege> userPrivileges;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<RolePrivilegeRelation> rolePrivilegeRelation;
}
