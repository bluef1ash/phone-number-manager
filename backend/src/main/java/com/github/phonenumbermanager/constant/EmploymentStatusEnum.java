package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 就业情况枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum EmploymentStatusEnum implements IEnum<Integer> {
    /**
     *
     */
    WORK(0, "在职"), RETIREMENT(1, "退休"), UNEMPLOYED(2, "无业");

    private final int value;
    private final String description;

    EmploymentStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
