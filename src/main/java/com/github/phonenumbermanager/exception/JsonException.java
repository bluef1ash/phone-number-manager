package com.github.phonenumbermanager.exception;


/**
 * 返回JSON异常
 *
 * @author 廿二月的天
 */
public class JsonException extends RuntimeException {
    private static final long serialVersionUID = -5847549478257193367L;

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
