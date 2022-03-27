package com.github.phonenumbermanager.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("系统用户传给前台对象")
public class SystemUserVo implements Serializable {
    @ApiModelProperty("系统用户编号")
    private Long id;
    @ApiModelProperty("系统用户名称")
    private String username;
    @ApiModelProperty("系统用户职务")
    private String[] positions;
    @ApiModelProperty("系统用户职称")
    private String[] titles;
    @ApiModelProperty("是否参加社区分包")
    private Boolean isSubcontract;
    @ApiModelProperty("系统用户登录时间")
    private LocalDateTime loginTime;
    @ApiModelProperty("系统用户登录IP地址")
    private String loginIp;
    @ApiModelProperty("系统用户是否被锁定")
    private Boolean isLocked;
    @ApiModelProperty("系统用户是否启用")
    private Boolean isEnabled;
    @ApiModelProperty("系统用户过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accountExpireTime;
    @ApiModelProperty("系统用户本次登录过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime credentialExpireTime;
    @ApiModelProperty("联系方式编号")
    private Long phoneNumberId;
    @ApiModelProperty("系统用户所属单位")
    private List<CompanyVo> companies;
    @ApiModelProperty("联系方式")
    private String phoneNumber;
}
