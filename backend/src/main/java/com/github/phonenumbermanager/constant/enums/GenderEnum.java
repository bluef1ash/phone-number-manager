package com.github.phonenumbermanager.constant.enums;

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
    MALE(0, "男"), FEMALE(1, "女"), UNKNOWN(2, "未知");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;
}
