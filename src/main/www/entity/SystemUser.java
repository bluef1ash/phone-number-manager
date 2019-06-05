package www.entity;

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
    public static final int LOCKED = 1;
    public static final int NON_LOCKED = 0;

    private static final long serialVersionUID = -5035917619026010434L;
    private Long systemUserId;
    private String username;
    private String password;
    private Timestamp loginTime = new Timestamp(1000);
    private String loginIp = "";
    private Integer isLocked;
    private Long roleId;
    private Long roleLocationId;
    private UserRole userRole;

    public SystemUser() {
    }

    public SystemUser(Long systemUserId, String username, String password, Timestamp loginTime, String loginIp, Integer isLocked, Long roleId, Long roleLocationId, UserRole userRole) {
        this.systemUserId = systemUserId;
        this.username = username;
        this.password = password;
        this.loginTime = loginTime;
        this.loginIp = loginIp;
        this.isLocked = isLocked;
        this.roleId = roleId;
        this.roleLocationId = roleLocationId;
        this.userRole = userRole;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Long systemUserId) {
        this.systemUserId = systemUserId;
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
        return isLocked == SystemUser.NON_LOCKED;
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
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(String.valueOf(userRole.getRoleId()));
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

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleLocationId() {
        return roleLocationId;
    }

    public void setRoleLocationId(Long roleLocationId) {
        this.roleLocationId = roleLocationId;
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
            isEqual = new EqualsBuilder().append(getRoleId(), systemUser.getRoleId()).append(getRoleLocationId(), systemUser.getRoleLocationId()).append(getSystemUserId(), systemUser.getSystemUserId()).append(getUsername(), systemUser.getUsername()).isEquals();
        }
        return isEqual;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getRoleId()).append(getRoleLocationId()).append(getSystemUserId()).append(getUsername()).toHashCode();
    }

    @Override
    public String toString() {
        return "SystemUser{" + "systemUserId=" + systemUserId + ", username='" + username + '\'' + ", password='" + password + '\'' + ", loginTime=" + loginTime + ", loginIp='" + loginIp + '\'' + ", isLocked=" + isLocked + ", roleId=" + roleId + ", roleLocationId=" + roleLocationId + '\'' + ", userRole=" + userRole + '}';
    }
}
