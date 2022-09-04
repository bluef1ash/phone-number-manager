package com.github.phonenumbermanager.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 社区居民与联系方式对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("社区居民与联系方式对象实体")
public class CommunityResidentPhoneNumber extends BaseEntity<CommunityResidentPhoneNumber> {
    @ApiModelProperty("所属社区居民编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属社区居民编号不能为空！")
    @Min(value = 0, message = "所属社区居民编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long communityResidentId;
    @ApiModelProperty("所属联系方式编号")
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "所属联系方式编号不能为空！")
    @Min(value = 0, message = "所属联系方式编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long phoneNumberId;
}
