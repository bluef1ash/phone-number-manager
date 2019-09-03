package com.github.phonenumbermanager.action;

import com.github.phonenumbermanager.exception.HttpStatusOkException;
import com.github.phonenumbermanager.exception.JsonException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制器钩子
 *
 * @author 廿二月的天
 */
@ControllerAdvice
public class ControllerAdvisor {

    /**
     * 异常显示
     *
     * @param model     前台模型
     * @param exception 异常对象
     * @return 异常页面
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String methodArgumentTypeMismatch(Model model, Exception exception) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("exception", exception);
        return "exception/default";
    }

    /**
     * HTTP正常状态异常显示视图
     *
     * @param model     前台模型
     * @param exception 异常对象
     * @return 异常页面
     */
    @ExceptionHandler(HttpStatusOkException.class)
    @ResponseStatus(HttpStatus.OK)
    public String httpStatusOk(Model model, HttpStatusOkException exception) {
        model.addAttribute("exception", exception);
        model.addAttribute("status", HttpStatus.OK.value());
        return "exception/default";
    }

    /**
     * 返回JSON异常显示视图
     *
     * @param exception 异常对象
     * @return JSON对象
     */
    @ExceptionHandler(JsonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> json(JsonException exception) {
        Map<String, Object> jsonMap = new HashMap<>(4);
        Map<String, String> messageError = new HashMap<>(2);
        messageError.put("defaultMessage", exception.getMessage());
        jsonMap.put("state", 0);
        jsonMap.put("messageError", messageError);
        jsonMap.put("status", HttpStatus.BAD_REQUEST.value());
        return jsonMap;
    }
}
