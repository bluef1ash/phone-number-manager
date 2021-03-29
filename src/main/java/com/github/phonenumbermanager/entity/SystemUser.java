package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.github.phonenumbermanager.constant.UserLevelEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 系统用户实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SystemUser implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Date loginTime;
    private String loginIp;
    @TableField("is_locked")
    private Boolean locked;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
    private UserLevelEnum level;
    private Long companyId;
    private Long roleId;
    @TableField(exist = false)
    private UserRole userRole;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == null || !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String role = userRole == null || userRole.getId() == null ? "null" : String.valueOf(userRole.getId());
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        grantedAuthorities.add(grantedAuthority);
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (object != null && SystemUser.class.isAssignableFrom(object.getClass())) {
            SystemUser systemUser = (SystemUser) object;
            isEqual = new EqualsBuilder().append(getRoleId(), systemUser.getRoleId()).append(getCompanyId(), systemUser.getCompanyId()).append(getId(), systemUser.getId()).append(getUsername(), systemUser.getUsername()).append(getLocked(), systemUser.getLocked()).append(getCreateTime(), systemUser.getCreateTime()).append(getUpdateTime(), systemUser.getUpdateTime()).append(getLevel(), systemUser.getLevel()).isEquals();
        }
        return isEqual;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getUsername()).append(getRoleId()).append(getLocked()).append(getCreateTime()).append(getUpdateTime()).append(getCompanyId()).append(getLevel()).toHashCode();
    }
}
