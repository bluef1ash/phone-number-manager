package com.github.phonenumbermanager.action;

import com.alibaba.fastjson.JSONObject;
import com.github.phonenumbermanager.exception.HttpStatusOkException;
import com.github.phonenumbermanager.exception.NotfoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 控制器钩子
 *
 * @author 廿二月的天
 */
@ControllerAdvice
public class ControllerAdvisor {
    public static final String X_REQUESTED_WITH = "x-requested-with";
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;

    /**
     * 异常显示
     *
     * @param model     前台模型
     * @param exception 异常对象
     * @return 异常页面
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String methodArgumentTypeMismatch(Model model, Exception exception) throws IOException {
        return output(model, exception, HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 404找不到异常显示
     *
     * @param model     前台模型
     * @param exception 异常对象
     * @return 异常页面
     */
    @ExceptionHandler(NotfoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notfound(Model model, Exception exception) throws IOException {
        return output(model, exception, HttpStatus.NOT_FOUND.value());
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
     * 输出异常结果
     *
     * @param model      前台模型
     * @param exception  异常对象
     * @param httpStatus HTTP状态
     * @return 异常结果
     * @throws IOException IO异常
     */
    private String output(Model model, Exception exception, int httpStatus) throws IOException {
        exception.printStackTrace();
        if (request.getHeader(X_REQUESTED_WITH) != null && XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeader(X_REQUESTED_WITH))) {
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
            return null;
        }
        model.addAttribute("status", httpStatus);
        model.addAttribute("exception", exception);
        return "exception/default";
    }
}
