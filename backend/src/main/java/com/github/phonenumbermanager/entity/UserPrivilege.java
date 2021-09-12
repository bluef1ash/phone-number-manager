package com.github.phonenumbermanager.entity;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户权限对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_privilege")
@ApiModel("用户权限对象实体")
public class UserPrivilege extends BaseEntity<UserPrivilege> implements GrantedAuthority {
    private Long id;
    private String name;
    private String description;
    private String uri;
    private Long parentId;
    private String iconName;
    private Integer orders;
    private Boolean isDisplay;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer level;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<UserRole> userRoles;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<UserPrivilege> subUserPrivileges;

    @Override
    public String getAuthority() {
        return uri;
    }
}
