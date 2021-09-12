package com.github.phonenumbermanager.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区居民对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("社区居民对象实体")
public class CommunityResident extends BaseEntity<CommunityResident> {
    private Long id;
    private String name;
    private String address;
    private Long subcontractorId;
    private Long communityId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Long> communityIds;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Community community;
}
