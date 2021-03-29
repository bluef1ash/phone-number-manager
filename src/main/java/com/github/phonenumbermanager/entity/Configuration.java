package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置项实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Configuration implements Serializable {
    @TableId(type = IdType.INPUT, value = "`key`")
    private String key;
    private Integer type;
    private String value;
    private String description;
    private Boolean keyIsChanged;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
}
