package com.github.phonenumbermanager.exception;


import org.springframework.security.authentication.AccountStatusException;

/**
 * 系统关闭异常
 *
 * @author 廿二月的天
 */
public class SystemClosedException extends AccountStatusException {

    public SystemClosedException(String msg) {
        super(msg);
    }

    public SystemClosedException(String msg, Throwable t) {
        super(msg, t);
    }
}
