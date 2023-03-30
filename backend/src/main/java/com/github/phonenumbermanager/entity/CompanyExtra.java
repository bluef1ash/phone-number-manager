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
 * 单位额外属性对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "单位额外属性对象实体")
public class CompanyExtra extends BaseEntity<CompanyExtra> {
    @Schema(title = "单位额外属性编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "单位额外属性标题")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性标题不能为空！")
    @Length(max = 100, message = "单位额外属性标题不能超过100个字符！")
    private String title;
    @Schema(title = "单位额外属性描述")
    @Length(max = 255, message = "单位额外属性描述不能超过255个字符！")
    private String description;
    @Schema(title = "单位额外属性变量名称")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性变量名称不能为空！")
    @Length(max = 100, message = "单位额外属性名称不能超过100个字符！")
    private String name;
    @Schema(title = "单位额外属性变量值")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性变量值不能为空！")
    private String content;
    @Schema(title = "单位额外属性字段类型")
    @NotNull(groups = CreateInputGroup.class, message = "单位额外属性字段类型不能为空！")
    private FieldTypeEnum fieldType;
    @Schema(title = "所属单位编号")
    @NotNull(groups = CreateInputGroup.class, message = "所属单位编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
}
