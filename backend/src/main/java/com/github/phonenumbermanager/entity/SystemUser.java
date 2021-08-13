package com.github.phonenumbermanager.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.github.phonenumbermanager.constant.UserLevelEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统用户对象实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("系统用户对象实体")
public class SystemUser implements UserDetails {
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    Collection<? extends GrantedAuthority> authorities;
    private Long id;
    private String username;
    private String password;
    private Date loginTime;
    private String loginIp;
    private Boolean isLocked;
    private Boolean isEnabled;
    @TableField(fill = FieldFill.INSERT)
    private Date accountExpireTime;
    @TableField(fill = FieldFill.INSERT)
    private Date credentialExpireTime;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
    private UserLevelEnum level;
    private Long companyId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<UserRole> userRoles;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return System.currentTimeMillis() < accountExpireTime.getTime();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return System.currentTimeMillis() < credentialExpireTime.getTime();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (object != null && SystemUser.class.isAssignableFrom(object.getClass())) {
            SystemUser systemUser = (SystemUser)object;
            isEqual = new EqualsBuilder().append(getCompanyId(), systemUser.getCompanyId())
                .append(getId(), systemUser.getId()).append(getUsername(), systemUser.getUsername())
                .append(isAccountNonLocked(), systemUser.isAccountNonLocked())
                .append(getCreateTime(), systemUser.getCreateTime()).append(getUpdateTime(), systemUser.getUpdateTime())
                .append(getLevel(), systemUser.getLevel())
                .append(isAccountNonExpired(), systemUser.isAccountNonLocked())
                .append(getCredentialExpireTime(), systemUser.getCredentialExpireTime())
                .append(getAccountExpireTime(), systemUser.getAccountExpireTime()).isEquals();
        }
        return isEqual;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getUsername()).append(isAccountNonLocked())
            .append(getCreateTime()).append(getUpdateTime()).append(getCompanyId()).append(getLevel())
            .append(isAccountNonExpired()).append(isCredentialsNonExpired()).append(isEnabled()).toHashCode();
    }
}
