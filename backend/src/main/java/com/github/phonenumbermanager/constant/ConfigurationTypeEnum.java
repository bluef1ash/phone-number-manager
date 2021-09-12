package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 配置类别枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum ConfigurationTypeEnum implements IEnum<Integer> {
    /**
     *
     */
    UNKNOWN(0, "未知类型"), BOOLEAN(1, "布尔类型"), STRING(2, "字符串类型"), NUMBER(3, "数值类型"), SYSTEM_USER(4, "系统用户类型");

    private final int value;
    private final String description;

    ConfigurationTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
