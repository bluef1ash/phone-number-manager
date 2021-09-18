package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单位级别枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum UserLevelEnum {
    /**
     *
     */
    ADMINISTRATOR(0, "管理员级别"), COMMUNITY(1, "社区级别"), SUBDISTRICT(2, "街道级别");

    @EnumValue
    private final int value;
    private final String description;
}
