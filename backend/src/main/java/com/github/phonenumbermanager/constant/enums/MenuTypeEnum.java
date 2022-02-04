package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum MenuTypeEnum {
    /**
     *
     */
    ALL(0, "全部"), FRONTEND(1, "前端"), BACKEND(2, "后端");

    @EnumValue
    private final int value;
    private final String description;
}
