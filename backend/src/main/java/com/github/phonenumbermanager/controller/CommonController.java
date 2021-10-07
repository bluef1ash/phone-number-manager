package com.github.phonenumbermanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.util.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 公共控制器
 *
 * @author 廿二月的天
 */
@RestController
@Api(tags = "公共控制器")
public class CommonController extends BaseController {

    /**
     * 未登录错误页面
     *
     * @return JSON数据
     */
    @GetMapping("/loginError")
    @ApiOperation("未登录错误页面")
    public R loginError() {
        throw new JsonException("您尚未登录，请登录后重试！");
    }
}
