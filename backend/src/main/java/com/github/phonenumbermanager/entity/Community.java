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
 * 社区对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("社区对象实体")
public class Community extends BaseEntity<Community> {
    private Long id;
    private String name;
    private Integer actualNumber;
    private Boolean dormitorySubmitted;
    private Boolean residentSubmitted;
    private Long subdistrictId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<CommunityResident> communityResidents;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Subdistrict subdistrict;
}
