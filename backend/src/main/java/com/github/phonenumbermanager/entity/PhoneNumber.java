package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.annotation.PhoneNumberValidator;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "联系方式对象实体")
public class PhoneNumber extends BaseEntity<PhoneNumber> {
    @Schema(title = "联系方式编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时联系方式编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "联系方式")
    @NotBlank(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "联系方式不能为空！")
    @PhoneNumberValidator
    private String phoneNumber;
    @Schema(title = "联系方式类型")
    @NotNull(groups = CreateInputGroup.class, message = "联系方式类型不能为空！")
    private PhoneTypeEnum phoneType;
}
