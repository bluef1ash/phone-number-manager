package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 性别枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum GenderEnum implements IEnum<Integer> {
    /**
     *
     */
    MALE(0, "男"), FEMALE(1, "女"), UNKNOWN(2, "未知");

    private final int value;
    private final String description;

    GenderEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
