package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 单位级别枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum UserLevelEnum implements IEnum<Integer> {
    /**
     *
     */
    ADMINISTRATOR(0, "管理员级别"), COMMUNITY(1, "社区级别"), SUBDISTRICT(2, "街道级别");

    @JsonValue
    private final int value;
    private final String description;

    UserLevelEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
