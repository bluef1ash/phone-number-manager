package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 联系方式来源类型枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum PhoneNumberSourceTypeEnum implements IEnum<Integer> {
    /**
     *
     */
    COMMUNITY_RESIDENT(0, "社区居民"), DORMITORY_MANAGER(1, "楼片长"), SUBCONTRACTOR(2, "分包人"), COMMUNITY(3, "社区"), SUBDISTRICT(4, "街道");

    private final int value;
    private final String description;

    PhoneNumberSourceTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
