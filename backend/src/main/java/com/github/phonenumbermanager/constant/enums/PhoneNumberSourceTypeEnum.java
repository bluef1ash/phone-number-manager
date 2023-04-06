package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 联系方式来源类型枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum PhoneNumberSourceTypeEnum {
    /**
     *
     */
    COMMUNITY_RESIDENT(0, "社区居民"), DORMITORY_MANAGER(1, "楼片长"), SUBCONTRACTOR(2, "分包人"), COMMUNITY(3, "社区"),
    STREET(4, "街道");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static PhoneNumberSourceTypeEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static PhoneNumberSourceTypeEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
