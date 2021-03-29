package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 社区分包人实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Subcontractor implements Serializable {
    private Long id;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
    private Long communityId;
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    private Community community;
}
