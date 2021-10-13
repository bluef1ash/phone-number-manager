package com.github.phonenumbermanager.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统用户与单位中间对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("系统用户与单位中间对象实体")
public class SystemUserCompany extends BaseEntity<SystemUserCompany> {
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "系统用户编号不能为空！")
    @Min(value = 0, message = "系统用户编号不正确！")
    private Long systemUserId;
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "单位编号不能为空！")
    @Min(value = 0, message = "单位编号不正确！")
    private Long companyId;
}
