package com.github.phonenumbermanager.vo;

import java.io.Serializable;

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
public class SystemUserLoginVo implements Serializable {
    @ApiModelProperty("系统用户联系方式")
    private String phoneNumber;
    @ApiModelProperty("系统用户密码")
    private String password;
    @ApiModelProperty("是否自动登录")
    private Boolean autoLogin;
}
