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
 * 单位与系统权限中间对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "单位与系统权限中间对象实体")
public class CompanyPermission extends BaseEntity<CompanyPermission> {
    @Schema(title = "单位编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "单位编号不能为空！")
    @Min(value = 0, message = "单位编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @Schema(title = "系统权限编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "系统权限编号不能为空！")
    @Min(value = 0, message = "系统权限编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long permissionId;
}
