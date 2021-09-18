package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限方法类型枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum HttpMethodEnum {
    /**
     *
     */
    ALL(0, "get,head,post,put,patch,delete,options,trace"), GET(1, "get"), HEAD(2, "head"), POST(3, "post"),
    PUT(4, "put"), PATCH(5, "patch"), DELETE(6, "delete"), OPTIONS(7, "options"), TRACE(8, "trace");

    @EnumValue
    private final int value;
    private final String description;
}
