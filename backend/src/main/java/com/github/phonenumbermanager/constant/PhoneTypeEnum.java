package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 联系方式类型枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum PhoneTypeEnum implements IEnum<Integer> {
    /**
     *
     */
    UNKNOWN(0, "未知"), MOBILE(1, "手机号码"), LANDLINE(2, "固定电话");

    private final int value;
    private final String description;

    PhoneTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
