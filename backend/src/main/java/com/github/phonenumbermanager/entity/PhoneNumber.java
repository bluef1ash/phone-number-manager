package com.github.phonenumbermanager.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
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
    @NotBlank(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "联系方式不能为空！")
    private String phoneNumber;
    @NotBlank(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "联系方式来源编号不能为空！")
    private String sourceId;
    @NotNull(groups = CreateInputGroup.class, message = "联系方式类型不能为空！")
    private PhoneTypeEnum phoneType;
}
