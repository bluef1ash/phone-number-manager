package com.github.phonenumbermanager.entity;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpMethod;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.constant.enums.MenuTypeEnum;
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
@TableName(autoResultMap = true)
@ApiModel("系统权限对象实体")
public class SystemPermission extends BaseEntity<SystemPermission> {
    @ApiModelProperty("系统权限编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @ApiModelProperty("系统权限名称")
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限名称不能为空！")
    @Length(max = 30, message = "系统权限名称不能超过30个字符")
    private String name;
    @ApiModelProperty("系统权限约束名称")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限约束名称不能为空！")
    @Length(max = 255, message = "系统权限约束名称不能超过255个字符")
    private String functionName;
    @ApiModelProperty("系统权限地址")
    @Length(max = 100, message = "系统权限地址不能超过255个字符")
    private String uri;
    @ApiModelProperty("系统权限方式")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private HttpMethod[] httpMethods;
    @ApiModelProperty("系统权限上级编号")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限上级编号不能为空！")
    @Min(value = 0, message = "系统权限上级编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @ApiModelProperty("排序")
    private Integer orderBy;
    @ApiModelProperty("系统权限菜单类型")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限菜单类型不能为空！")
    private MenuTypeEnum menuType;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限是否显示不能为空！")
    @ApiModelProperty("系统权限是否显示")
    private Boolean isDisplay;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Company> companies;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<SystemPermission> children;
}
