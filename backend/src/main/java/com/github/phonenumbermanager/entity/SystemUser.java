package com.github.phonenumbermanager.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统用户对象实体
 *
 * @author 廿二月的天
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("系统用户对象实体")
@TableName(autoResultMap = true)
public class SystemUser extends BaseEntity<SystemUser> implements UserDetails {
    @ApiModelProperty("系统用户编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @ApiModelProperty("系统用户名称")
    @NotBlank(groups = CreateInputGroup.class, message = "系统用户名称不能为空！")
    @Length(max = 10, message = "系统用户名称不允许超过10个字符！")
    private String username;
    @ApiModelProperty("系统用户密码")
    @Length(min = 6, message = "系统用户密码不允许少于6个字符！")
    private String password;
    @ApiModelProperty("系统用户登录时间")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime loginTime;
    @ApiModelProperty("系统用户登录IP地址")
    private String loginIp;
    @ApiModelProperty("系统用户是否被锁定")
    @NotNull(groups = CreateInputGroup.class, message = "系统用户是否被锁定不能为空！")
    private Boolean isLocked;
    @ApiModelProperty("系统用户是否启用")
    @NotNull(groups = CreateInputGroup.class, message = "用户是否开启不能为空！")
    private Boolean isEnabled;
    @ApiModelProperty("系统用户过期时间")
    @TableField(fill = FieldFill.INSERT)
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime accountExpireTime;
    @ApiModelProperty("系统用户本次登录过期时间")
    @TableField(fill = FieldFill.INSERT)
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime credentialExpireTime;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Company> companies;
    @ApiModelProperty("系统用户联系方式")
    @TableField(exist = false)
    private PhoneNumber phoneNumber;

    @Override
    public boolean isAccountNonExpired() {
        return accountExpireTime != null && LocalDateTime.now().isBefore(accountExpireTime);
    }

    @Override
    public boolean isAccountNonLocked() {
        return isLocked != null && !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialExpireTime != null && LocalDateTime.now().isBefore(credentialExpireTime);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return companies;
    }
}
