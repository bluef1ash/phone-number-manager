package com.github.phonenumbermanager.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.ConfigurationFieldTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 配置项对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("配置项对象实体")
public class Configuration extends BaseEntity<Configuration> {
    @ApiModelProperty("配置编号")
    @NotBlank(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @ApiModelProperty("配置标题")
    @NotBlank(groups = CreateInputGroup.class, message = "配置标题不能为空！")
    private String title;
    @ApiModelProperty("配置描述")
    @NotBlank(groups = CreateInputGroup.class, message = "配置描述不能为空！")
    private String description;
    @ApiModelProperty("配置变量名称")
    @NotNull(groups = CreateInputGroup.class, message = "配置变量名称不能为空！")
    private String name;
    @ApiModelProperty("配置变量值")
    @NotNull(groups = CreateInputGroup.class, message = "配置变量值不能为空！")
    private String content;
    @ApiModelProperty("配置字段类型")
    @NotNull(groups = CreateInputGroup.class, message = "配置字段类型不能为空！")
    private ConfigurationFieldTypeEnum fieldType;
    @ApiModelProperty("配置字段值")
    @NotBlank(groups = CreateInputGroup.class, message = "配置字段值不能为空！")
    private String fieldValue;
    @ApiModelProperty("排序")
    private Integer orderBy;
}
