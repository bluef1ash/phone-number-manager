package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 联系方式类型枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum PhoneTypeEnum {
    /**
     *
     */
    UNKNOWN(0, "未知"), MOBILE(1, "手机号码"), LANDLINE(2, "固定电话");

    @EnumValue
    private final int value;
    private final String description;
}
