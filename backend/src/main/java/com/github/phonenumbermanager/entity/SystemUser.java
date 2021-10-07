package com.github.phonenumbermanager.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
public class SystemUser extends BaseEntity<SystemUser> implements UserDetails {
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    Collection<? extends GrantedAuthority> authorities;
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @NotBlank(groups = CreateInputGroup.class, message = "用户名称不能为空！")
    @Length(max = 10, message = "系统用户名称不允许超过10个字符！")
    private String username;
    @NotBlank(groups = CreateInputGroup.class, message = "用户密码不能为空！")
    private String password;
    @Past
    private Date loginTime;
    private String loginIp;
    @NotNull(groups = CreateInputGroup.class, message = "用户是否被锁定不能为空！")
    private Boolean isLocked;
    @NotBlank(groups = CreateInputGroup.class, message = "用户是否开启不能为空！")
    private Boolean isEnabled;
    @TableField(fill = FieldFill.INSERT)
    private Date accountExpireTime;
    @TableField(fill = FieldFill.INSERT)
    private Date credentialExpireTime;
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
            isEqual = new EqualsBuilder().append(getId(), systemUser.getId())
                .append(getUsername(), systemUser.getUsername())
                .append(isAccountNonLocked(), systemUser.isAccountNonLocked())
                .append(getCreateTime(), systemUser.getCreateTime()).append(getUpdateTime(), systemUser.getUpdateTime())
                .append(isAccountNonExpired(), systemUser.isAccountNonLocked())
                .append(getCredentialExpireTime(), systemUser.getCredentialExpireTime())
                .append(getAccountExpireTime(), systemUser.getAccountExpireTime()).isEquals();
        }
        return isEqual;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getUsername()).append(isAccountNonLocked())
            .append(getCreateTime()).append(getUpdateTime()).append(isAccountNonExpired())
            .append(isCredentialsNonExpired()).append(isEnabled()).toHashCode();
    }
}
