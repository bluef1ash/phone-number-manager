package com.github.phonenumbermanager.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 基础实体类
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
public abstract class BaseEntity<T extends Model<?>> extends Model<T> {
    @ApiModelProperty("添加时间")
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
    @ApiModelProperty("更改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;
    @ApiModelProperty("乐观锁")
    @Version
    protected Integer version;
}
