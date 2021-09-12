package com.github.phonenumbermanager.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;

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
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;
    @Version
    @TableField
    protected Integer version;
}
