package com.github.phonenumbermanager.config;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.exception.HttpStatusOkException;
import com.github.phonenumbermanager.exception.NotfoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 控制器钩子
 *
 * @author 廿二月的天
 */
@RestControllerAdvice(basePackages = "com.github.phonenumbermanager.controller")
public class RestControllerAdvisor {
    @Resource
    private HttpServletResponse response;

    /**
     * 异常显示
     *
     * @param exception 异常对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void methodArgumentTypeMismatch(Exception exception) throws IOException {
        output(exception, HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 404找不到异常显示
     *
     * @param exception 异常对象
     */
    @ExceptionHandler(NotfoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notfound(Exception exception) throws IOException {
        output(exception, HttpStatus.NOT_FOUND.value());
    }

    /**
     * HTTP正常状态异常显示视图
     *
     * @param exception 异常对象
     */
    @ExceptionHandler(HttpStatusOkException.class)
    @ResponseStatus(HttpStatus.OK)
    public void httpStatusOk(HttpStatusOkException exception) throws IOException {
        output(exception, HttpStatus.OK.value());
    }

    /**
     * 输出异常结果
     *
     * @param exception  异常对象
     * @param httpStatus HTTP状态
     * @throws IOException IO异常
     */
    private void output(Exception exception, int httpStatus) throws IOException {
        exception.printStackTrace();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json; charset=utf-8");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        JSONObject returnObj = new JSONObject();
        returnObj.put("state", 0);
        returnObj.put("messageError", exception.getMessage());
        returnObj.put("status", httpStatus);
        PrintWriter writer = response.getWriter();
        writer.write(returnObj.toJSONString());
        writer.flush();
    }
}
