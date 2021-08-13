package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 权限方法类型枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum HttpMethodEnum implements IEnum<Integer> {
    /**
     *
     */
    ALL(0, "get,head,post,put,patch,delete,options,trace"), GET(1, "get"), HEAD(2, "head"), POST(3, "post"),
    PUT(4, "put"), PATCH(5, "patch"), DELETE(6, "delete"), OPTIONS(7, "options"), TRACE(8, "trace");

    private final int value;
    private final String description;

    HttpMethodEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
