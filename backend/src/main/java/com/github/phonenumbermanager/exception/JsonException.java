package com.github.phonenumbermanager.exception;

/**
 * 返回JSON异常
 *
 * @author 廿二月的天
 */
public class JsonException extends RuntimeException {

    public JsonException() {
        super();
    }

    public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
