package com.github.phonenumbermanager.entity;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.constant.enums.FieldTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统配置项对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "系统配置项对象实体")
public class Configuration extends BaseEntity<Configuration> {
    @Schema(title = "系统配置编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "系统配置标题")
    @NotBlank(groups = CreateInputGroup.class, message = "系统配置标题不能为空！")
    @Length(max = 100, message = "系统配置标题不能超过100个字符！")
    private String title;
    @Schema(title = "系统配置描述")
    @Length(max = 255, message = "系统配置描述不能超过255个字符！")
    private String description;
    @Schema(title = "系统配置变量名称")
    @NotBlank(groups = CreateInputGroup.class, message = "系统配置变量名称不能为空！")
    @Length(max = 100, message = "系统配置名称不能超过100个字符！")
    private String name;
    @Schema(title = "系统配置变量值")
    @NotBlank(groups = CreateInputGroup.class, message = "系统配置变量值不能为空！")
    private String content;
    @Schema(title = "系统配置字段类型")
    @NotNull(groups = CreateInputGroup.class, message = "系统配置字段类型不能为空！")
    private FieldTypeEnum fieldType;
    @Schema(title = "系统配置字段值")
    @Length(max = 255, message = "系统配置字段值不能超过255个字符！")
    private String fieldValue;
    @Schema(title = "排序")
    private Integer orderBy;
}
