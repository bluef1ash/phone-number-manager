package com.github.phonenumbermanager.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 异常代码
 *
 * @author 廿二月的天
 */
@Getter
@AllArgsConstructor
@ToString
public enum ExceptionCode {
    /**
     *
     */
    NOT_LOGGED(10000, "您尚未登录，请登录后重试！"), ADD_FAILED(10001, "数据添加失败！"), EDIT_FAILED(10002, "数据编辑失败！"),
    DELETE_FAILED(10003, "数据删除失败！"), NOT_MODIFIED(10004, "数据不允许修改！"), METHOD_ARGUMENT_NOT_VALID(10005, "数据校验失败！"),
    UNKNOWN_EXCEPTION(10006, "出现未知异常，请稍后重试！");

    private final int code;
    private final String description;
}
