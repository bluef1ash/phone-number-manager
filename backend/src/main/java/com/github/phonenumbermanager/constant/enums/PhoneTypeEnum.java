package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

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
    UNKNOWN(0, "未知"), MOBILE(1, "手机号码"), FIXED_LINE(2, "固定电话");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static PhoneTypeEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static PhoneTypeEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
