package com.github.phonenumbermanager.entity;

import com.github.phonenumbermanager.constant.enums.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;

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
    private String phoneNumber;
    private PhoneNumberSourceTypeEnum sourceType;
    private String sourceId;
    private PhoneTypeEnum phoneType;
}
