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
 * 单位对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("单位对象实体")
public class Company extends BaseEntity<Company> {
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "单位名称不能为空！")
    @Length(max = 10, message = "单位名称不能超过10个字符！")
    private String name;
    @NotNull(groups = CreateInputGroup.class, message = "单位分包数不能为空！")
    @Min(value = 1, message = "单位完成数量不正确！")
    private Integer actualNumber;
    @NotNull(groups = CreateInputGroup.class, message = "上级单位不能为空！")
    @Min(value = 0, message = "上级单位编号不正确！")
    private Long parentId;
    @PhoneNumberValidator
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Company> subCompanies;
}
