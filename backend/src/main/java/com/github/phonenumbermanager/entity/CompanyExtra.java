package com.github.phonenumbermanager.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.FieldTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("单位额外属性对象实体")
public class CompanyExtra extends BaseEntity<CompanyExtra> {
    @ApiModelProperty("单位额外属性编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @ApiModelProperty("单位额外属性标题")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性标题不能为空！")
    @Length(max = 100, message = "单位额外属性标题不能超过100个字符！")
    private String title;
    @ApiModelProperty("单位额外属性描述")
    @Length(max = 255, message = "单位额外属性描述不能超过255个字符！")
    private String description;
    @ApiModelProperty("单位额外属性变量名称")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性变量名称不能为空！")
    @Length(max = 100, message = "单位额外属性名称不能超过100个字符！")
    private String name;
    @ApiModelProperty("单位额外属性变量值")
    @NotBlank(groups = CreateInputGroup.class, message = "单位额外属性变量值不能为空！")
    private String content;
    @ApiModelProperty("单位额外属性字段类型")
    @NotNull(groups = CreateInputGroup.class, message = "单位额外属性字段类型不能为空！")
    private FieldTypeEnum fieldType;
    @ApiModelProperty("所属单位编号")
    @NotNull(groups = CreateInputGroup.class, message = "所属单位编号不能为空！")
    private Long companyId;
}
