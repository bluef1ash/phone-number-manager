package com.github.phonenumbermanager.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.phonenumbermanager.constant.ExceptionCode;
import com.github.phonenumbermanager.exception.HttpStatusOkException;
import com.github.phonenumbermanager.util.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 控制器钩子
 *
 * @author 廿二月的天
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.github.phonenumbermanager.controller")
public class RestControllerAdvisor {

    /**
     * 方法验证失败
     *
     * @param exception
     *            异常对象
     * @return json对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R methodArgumentNotValid(MethodArgumentNotValidException exception) {
        log.error("An exception occurs and the behavior is terminated", exception);
        Map<String, String> errorMap = new HashMap<>(exception.getBindingResult().getAllErrors().size());
        exception.getBindingResult().getFieldErrors()
            .forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return R.error(ExceptionCode.METHOD_ARGUMENT_NOT_VALID.getCode(),
            ExceptionCode.METHOD_ARGUMENT_NOT_VALID.getDescription()).put("exception", errorMap);
    }

    /**
     * 异常显示
     *
     * @param throwable
     *            异常对象
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R methodArgumentTypeMismatch(Throwable throwable) {
        log.error("An exception occurs and the behavior is terminated", throwable);
        return R.error(ExceptionCode.UNKNOWN_EXCEPTION.getCode(), throwable.getMessage()).put("defaultMessage",
            ExceptionCode.UNKNOWN_EXCEPTION.getDescription());
    }

    /**
     * HTTP正常状态异常显示视图
     *
     * @param exception
     *            异常对象
     * @return json对象
     */
    @ExceptionHandler(HttpStatusOkException.class)
    @ResponseStatus(HttpStatus.OK)
    public R httpStatusOk(HttpStatusOkException exception) {
        return R.error(HttpStatus.OK.value(), exception.getMessage());
    }
}
