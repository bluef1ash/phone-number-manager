package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 配置项实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class Configuration implements Serializable {
    private String key;
    private Integer type;
    private String value;
    private String description;
    private Boolean keyChanged;
    private Timestamp createTime;
    private Timestamp updateTime;
}
