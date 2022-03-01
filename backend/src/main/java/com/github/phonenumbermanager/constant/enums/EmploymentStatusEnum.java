package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 就业情况枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum EmploymentStatusEnum {
    /**
     *
     */
    WORK(0, "在职"), RETIREMENT(1, "退休"), UNEMPLOYED(2, "无业");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;
}
