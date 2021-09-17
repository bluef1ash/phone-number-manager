package com.github.phonenumbermanager.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.phonenumbermanager.exception.HttpStatusOkException;
import com.github.phonenumbermanager.exception.NotfoundException;
import com.github.phonenumbermanager.util.R;

/**
 * 控制器钩子
 *
 * @author 廿二月的天
 */
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
        exception.printStackTrace();
        Map<String, String> errorMap = new HashMap<>(exception.getBindingResult().getAllErrors().size());
        exception.getBindingResult().getAllErrors()
            .forEach(error -> errorMap.put(error.getObjectName(), error.getDefaultMessage()));
        return R.error(HttpStatus.BAD_REQUEST.value(), "参数错误").put("error", errorMap);
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
        throwable.printStackTrace();
        return R.error(HttpStatus.BAD_REQUEST.value(), throwable.getMessage());
    }

    /**
     * 404找不到异常显示
     *
     * @param exception
     *            异常对象
     * @return json对象
     */
    @ExceptionHandler(NotfoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R notfound(NotfoundException exception) {
        exception.printStackTrace();
        return R.error(HttpStatus.NOT_FOUND.value(), exception.getMessage());
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
