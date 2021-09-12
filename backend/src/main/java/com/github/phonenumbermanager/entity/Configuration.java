package com.github.phonenumbermanager.entity;

import com.github.phonenumbermanager.constant.ConfigurationTypeEnum;

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
    private String key;
    private ConfigurationTypeEnum type;
    private String value;
    private String description;
    private Boolean keyIsChanged;
}
