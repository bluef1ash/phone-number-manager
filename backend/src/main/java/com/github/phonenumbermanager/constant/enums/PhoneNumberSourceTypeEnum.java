package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

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
    SUBDISTRICT(4, "街道");

    @EnumValue
    private final int value;
    private final String description;
}
