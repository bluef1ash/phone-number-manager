package com.github.phonenumbermanager.entity;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.validator.PhoneNumberValidator;

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
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民名称不能为空！")
    @Length(max = 10, message = "社区居民姓名不能超过10个字符！")
    private String name;
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民地址不能为空！")
    @Length(max = 255, message = "社区居民地址不能超过255个字符！")
    private String address;
    @NotNull(groups = CreateInputGroup.class, message = "社区分包人不能为空！")
    @Min(value = 1, message = "所属社区分包人编号不正确！")
    private Long systemUserId;
    @NotNull(groups = CreateInputGroup.class, message = "社区不能为空！")
    @Min(value = 1, message = "所属社区编号不正确！")
    private Long companyId;
    @PhoneNumberValidator
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private SystemUser systemUser;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Long> communityIds;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Company company;
}
