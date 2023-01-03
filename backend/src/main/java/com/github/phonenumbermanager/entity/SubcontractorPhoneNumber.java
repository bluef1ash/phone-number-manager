package com.github.phonenumbermanager.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区分包人员与联系方式中间对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "社区分包人员与联系方式中间对象实体")
public class SubcontractorPhoneNumber extends BaseEntity<SubcontractorPhoneNumber> {
    @Schema(title = "所属社区分包人员编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属社区分包人员编号不能为空！")
    @Min(value = 0, message = "所属社区分包人员编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subcontractorId;
    @Schema(title = "所属联系方式编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属联系方式编号不能为空！")
    @Min(value = 0, message = "所属联系方式编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long phoneNumberId;
}
