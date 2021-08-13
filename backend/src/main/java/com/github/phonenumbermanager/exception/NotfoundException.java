package com.github.phonenumbermanager.exception;

/**
 * 404找不到异常
 *
 * @author 廿二月的天
 */
public class NotfoundException extends RuntimeException {

    public NotfoundException() {
        super();
    }

    public NotfoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotfoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotfoundException(String message) {
        super(message);
    }

    public NotfoundException(Throwable cause) {
        super(cause);
    }
}
