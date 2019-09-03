package com.github.phonenumbermanager.entity;

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
public class SystemUser implements UserDetails {
    private static final long serialVersionUID = -5035917619026010434L;
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

    public SystemUser() {
    }

    public SystemUser(Long id, String username, String password, Timestamp loginTime, String loginIp, Boolean locked, Timestamp createTime, Timestamp updateTime, Integer companyType, Long companyId, Long roleId, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.loginTime = loginTime;
        this.loginIp = loginIp;
        this.locked = locked;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.companyType = companyType;
        this.companyId = companyId;
        this.roleId = roleId;
        this.userRole = userRole;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setUsername(String username) {
        this.username = username;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
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

    @Override
    public String toString() {
        return "SystemUser{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\'' + ", loginTime=" + loginTime + ", loginIp='" + loginIp + '\'' + ", locked=" + locked + ", createTime=" + createTime + ", updateTime=" + updateTime + ", companyType=" + companyType + ", companyId=" + companyId + ", roleId=" + roleId + ", userRole=" + userRole + '}';
    }
}
