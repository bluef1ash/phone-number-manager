package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类别枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum ConfigurationTypeEnum {
    /**
     *
     */
    UNKNOWN(0, "未知类型"), BOOLEAN(1, "布尔类型"), STRING(2, "字符串类型"), NUMBER(3, "数值类型"), SYSTEM_USER(4, "系统用户类型");

    @EnumValue
    private final int value;
    private final String description;
}
