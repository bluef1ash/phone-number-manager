package com.github.phonenumbermanager.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

/**
 * 返回结果对象
 *
 * @author 廿二月的天
 */
public class R extends HashMap<String, Object> {

    public R() {
        put("code", 0);
        put("message", "success");
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static R error(String message) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
    }

    public static R error(int code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        return r;
    }

    public static R ok(String message) {
        R r = new R();
        r.put("message", message);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
