package com.github.phonenumbermanager.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户权限对象实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_privilege")
@ApiModel("用户权限对象实体")
public class UserPrivilege implements GrantedAuthority {
    private Long id;
    private String name;
    private String description;
    private String uri;
    private Long parentId;
    private String iconName;
    private Integer orders;
    private Boolean isDisplay;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
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
