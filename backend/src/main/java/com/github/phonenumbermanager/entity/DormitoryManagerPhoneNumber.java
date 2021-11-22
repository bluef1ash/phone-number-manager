package com.github.phonenumbermanager.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 楼片长信息与联系方式对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("楼片长信息与联系方式对象实体")
public class DormitoryManagerPhoneNumber extends BaseEntity<DormitoryManagerPhoneNumber> {
    @ApiModelProperty("所属楼片长信息编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属楼片长信息编号不能为空！")
    @Min(value = 0, message = "所属楼片长信息编号不正确！")
    private Long dormitoryManagerId;
    @ApiModelProperty("所属联系方式编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属联系方式编号不能为空！")
    @Min(value = 0, message = "所属联系方式编号不正确！")
    private Long phoneNumberId;
}
