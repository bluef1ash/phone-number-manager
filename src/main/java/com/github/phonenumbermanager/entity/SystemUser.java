package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 系统用户实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class SystemUser implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Timestamp loginTime;
    private String loginIp = "";
    private Boolean locked;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer companyType;
    private Long companyId;
    private Long roleId;
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
            isEqual = new EqualsBuilder().append(getRoleId(), systemUser.getRoleId()).append(getCompanyId(), systemUser.getCompanyId()).append(getId(), systemUser.getId()).append(getUsername(), systemUser.getUsername()).append(getLocked(), systemUser.getLocked()).append(getCreateTime(), systemUser.getCreateTime()).append(getUpdateTime(), systemUser.getUpdateTime()).append(getCompanyType(), systemUser.getCompanyType()).isEquals();
        }
        return isEqual;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getUsername()).append(getRoleId()).append(getLocked()).append(getCreateTime()).append(getUpdateTime()).append(getCompanyId()).append(getCompanyType()).toHashCode();
    }
}
