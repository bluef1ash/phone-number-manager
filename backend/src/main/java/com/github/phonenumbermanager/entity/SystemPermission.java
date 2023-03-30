package com.github.phonenumbermanager.entity;

import java.util.List;

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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(autoResultMap = true)
@Schema(title = "系统权限对象实体")
public class SystemPermission extends BaseEntity<SystemPermission> {
    @Schema(title = "系统权限编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "系统权限名称")
    @NotBlank(groups = CreateInputGroup.class, message = "系统权限名称不能为空！")
    @Length(max = 30, message = "系统权限名称不能超过30个字符")
    private String name;
    @Schema(title = "系统权限约束名称")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限约束名称不能为空！")
    @Length(max = 255, message = "系统权限约束名称不能超过255个字符")
    private String functionName;
    @Schema(title = "系统权限地址")
    @Length(max = 100, message = "系统权限地址不能超过255个字符")
    private String uri;
    @Schema(title = "系统权限方式")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private HttpMethod[] httpMethods;
    @Schema(title = "系统权限上级编号")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限上级编号不能为空！")
    @Min(value = 0, message = "系统权限上级编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @Schema(title = "排序")
    private Integer orderBy;
    @Schema(title = "系统权限菜单类型")
    @NotNull(groups = CreateInputGroup.class, message = "系统权限菜单类型不能为空！")
    private MenuTypeEnum menuType;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限是否显示不能为空！")
    @Schema(title = "系统权限是否显示")
    private Boolean isDisplay;
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<Company> companies;
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<SystemPermission> children;
}
