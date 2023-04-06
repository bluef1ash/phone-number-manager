package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统用户登录对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("系统用户登录对象")
public class SystemUserLoginVO implements Serializable {
    @ApiModelProperty("系统用户名称")
    @NotBlank(message = "系统用户名称不能为空！")
    private String username;
    @ApiModelProperty("系统用户密码")
    @NotBlank(message = "系统用户密码不能为空！")
    private String password;
    @ApiModelProperty("登录图形验证码")
    @NotBlank(message = "登录图形验证码不能为空！")
    private String captcha;
    @ApiModelProperty("是否自动登录")
    private Boolean autoLogin;
    @ApiModelProperty("图形验证码缓存编号")
    @NotBlank(message = "登录图形验证码编号不能为空！")
    private String captchaId;
}
