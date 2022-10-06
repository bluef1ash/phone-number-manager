package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum GenderEnum {
    /**
     *
     */
    FEMALE(0, "女"), MALE(1, "男"), UNKNOWN(2, "未知");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static GenderEnum getEnum(int value) {
        return Arrays.stream(values()).filter(e -> e.getValue() == value).findFirst().orElse(null);
    }
}
