package com.github.phonenumbermanager.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("单位与系统权限中间对象实体")
public class CompanyPermission extends BaseEntity<CompanyPermission> {
    @ApiModelProperty("单位编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "单位编号不能为空！")
    @Min(value = 0, message = "单位编号不正确！")
    private Long companyId;
    @ApiModelProperty("系统权限编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "系统权限编号不能为空！")
    @Min(value = 0, message = "系统权限编号不正确！")
    private Long permissionId;
}
