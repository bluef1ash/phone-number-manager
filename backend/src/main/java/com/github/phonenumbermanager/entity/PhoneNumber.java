package com.github.phonenumbermanager.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
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
 * 联系方式对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("联系方式对象实体")
public class PhoneNumber extends BaseEntity<PhoneNumber> {
    @ApiModelProperty("联系方式编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时联系方式编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @ApiModelProperty("联系方式")
    @NotBlank(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "联系方式不能为空！")
    @PhoneNumberValidator
    private String phoneNumber;
    @ApiModelProperty("联系方式类型")
    @NotNull(groups = CreateInputGroup.class, message = "联系方式类型不能为空！")
    private PhoneTypeEnum phoneType;
}
