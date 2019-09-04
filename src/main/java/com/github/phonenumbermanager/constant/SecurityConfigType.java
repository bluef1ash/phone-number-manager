package com.github.phonenumbermanager.constant;

/**
 * SecurityConfig类型标记
 *
 * @author 廿二月的天
 */
public enum SecurityConfigType {
    /**
     * 没有登录
     */
    NOT_LOGGED(0),
    /**
     * 没有匹配
     */
    NOT_MATCH(1);

    private int code;

    SecurityConfigType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }

    public int getCode() {
        return code;
    }
}
