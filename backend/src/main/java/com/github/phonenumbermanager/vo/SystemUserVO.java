package com.github.phonenumbermanager.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统用户传给前台对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@Schema(title = "系统用户传给前台对象")
public class SystemUserVO implements Serializable {
    @Schema(title = "系统用户编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(title = "系统用户名称")
    private String username;
    @Schema(title = "系统用户登录时间")
    private LocalDateTime loginTime;
    @Schema(title = "系统用户登录IP地址")
    private String loginIp;
    @Schema(title = "系统用户是否被锁定")
    private Boolean isLocked;
    @Schema(title = "系统用户是否启用")
    private Boolean isEnabled;
    @Schema(title = "系统用户过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accountExpireTime;
    @Schema(title = "系统用户本次登录过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime credentialExpireTime;
    @Schema(title = "系统用户所属单位")
    private List<CompanyVO> companyVOs;
}
