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
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "社区名称不能为空！")
    @Length(max = 10, message = "社区名称不能超过10个字符！")
    private String name;
    @NotNull(groups = CreateInputGroup.class, message = "社区分包数不能为空！")
    @Min(value = 1, message = "社区完成数量不正确！")
    private Integer actualNumber;
    private Boolean dormitorySubmitted;
    private Boolean residentSubmitted;
    @NotNull(groups = CreateInputGroup.class, message = "所属街道不能为空！")
    @Min(value = 1, message = "所属街道编号不正确！")
    private Long subdistrictId;
    @PhoneNumberValidator
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
