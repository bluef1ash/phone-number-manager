package com.github.phonenumbermanager.entity;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
@TableName("pm_system_role")
@ApiModel("用户角色对象实体")
public class UserRole extends BaseEntity<UserRole> implements GrantedAuthority {
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @Min(value = 0, message = "编号不正确！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "用户角色名称不能为空！")
    @Length(max = 10, message = "用户角色名称不允许超过10个字符！")
    private String name;
    @NotBlank(groups = CreateInputGroup.class, message = "用户角色描述不能为空！")
    @Length(max = 50, message = "用户角色描述不允许超过10个字符！")
    private String description;
    @NotNull(groups = CreateInputGroup.class, message = "用户角色父级编号不能为空！")
    @Min(value = 0, message = "用户角色父级编号不正确！")
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

    @Override
    public String getAuthority() {
        return String.valueOf(id);
    }
}
