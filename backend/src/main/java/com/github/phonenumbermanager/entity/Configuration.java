package com.github.phonenumbermanager.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.ConfigurationTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
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
    @NotBlank(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId(type = IdType.INPUT, value = "`key`")
    private String key;
    @NotNull(groups = CreateInputGroup.class, message = "配置类型不能为空！")
    private ConfigurationTypeEnum type;
    @NotBlank(groups = CreateInputGroup.class, message = "配置值不能为空！")
    private String value;
    @NotBlank(groups = CreateInputGroup.class, message = "配置描述不能为空！")
    private String description;
    @NotNull(groups = CreateInputGroup.class, message = "配置是否更改不能为空！")
    private Boolean keyIsChanged;
}
