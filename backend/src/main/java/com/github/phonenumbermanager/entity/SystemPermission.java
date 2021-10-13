package com.github.phonenumbermanager.entity;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.HttpMethodEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统权限对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("系统权限对象实体")
public class SystemPermission extends BaseEntity<SystemPermission> {
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限名称不能为空！")
    private String name;
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限约束名称不能为空！")
    private String description;
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限地址不能为空！")
    private String uri;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限方式不能为空！")
    private HttpMethodEnum httpMethod;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限父级编号不能为空！")
    @Min(value = 0, message = "系统权限父级编号不正确！")
    private Long parentId;
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限图标名称不能为空！")
    private String iconName;
    @NotNull(groups = CreateInputGroup.class, message = "排列顺序不能为空！")
    @Min(value = 0, message = "排列不正确！")
    private Integer orders;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限是否显示不能为空！")
    private Boolean isDisplay;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer level;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Company> companies;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<SystemPermission> children;
}
