package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(title = "系统用户登录对象")
public class SystemUserLoginVO implements Serializable {
    @Schema(title = "系统用户名称")
    @NotBlank(message = "系统用户名称不能为空！")
    private String username;
    @Schema(title = "系统用户密码")
    @NotBlank(message = "系统用户密码不能为空！")
    private String password;
    @Schema(title = "登录图形验证码")
    @NotBlank(message = "登录图形验证码不能为空！")
    private String captcha;
    @Schema(title = "是否自动登录")
    private Boolean autoLogin;
    @Schema(title = "图形验证码缓存编号")
    @NotBlank(message = "登录图形验证码编号不能为空！")
    private String captchaId;
}
