package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据类别枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum FieldTypeEnum {
    /**
     *
     */
    UNKNOWN(0, "未知类型"), BOOLEAN(1, "布尔类型"), STRING(2, "字符串类型"), NUMBER(3, "数值类型"), SYSTEM_USER(4, "系统用户类型");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static FieldTypeEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static FieldTypeEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
