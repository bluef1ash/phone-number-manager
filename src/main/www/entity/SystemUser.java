package www.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 系统用户实体
 *
 * @author 廿二月的天
 */
public class SystemUser implements Serializable, UserDetails {
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
    private String captcha;
    private UserRole userRole;

    public SystemUser() {
    }

    public SystemUser(Long systemUserId, String username, String password, Timestamp loginTime, String loginIp, Integer isLocked, Long roleId, Long roleLocationId, String captcha, UserRole userRole) {
        this.systemUserId = systemUserId;
        this.username = username;
        this.password = password;
        this.loginTime = loginTime;
        this.loginIp = loginIp;
        this.isLocked = isLocked;
        this.roleId = roleId;
        this.roleLocationId = roleLocationId;
        this.captcha = captcha;
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

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SystemUser that = (SystemUser) o;

        if (!Objects.equals(systemUserId, that.systemUserId)) {
            return false;
        }
        if (!Objects.equals(username, that.username)) {
            return false;
        }
        if (!Objects.equals(password, that.password)) {
            return false;
        }
        if (!Objects.equals(loginTime, that.loginTime)) {
            return false;
        }
        if (!Objects.equals(loginIp, that.loginIp)) {
            return false;
        }
        if (!Objects.equals(isLocked, that.isLocked)) {
            return false;
        }
        if (!Objects.equals(roleId, that.roleId)) {
            return false;
        }
        if (!Objects.equals(roleLocationId, that.roleLocationId)) {
            return false;
        }
        if (!Objects.equals(captcha, that.captcha)) {
            return false;
        }
        return Objects.equals(userRole, that.userRole);

    }

    @Override
    public int hashCode() {
        int result = systemUserId != null ? systemUserId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (loginTime != null ? loginTime.hashCode() : 0);
        result = 31 * result + (loginIp != null ? loginIp.hashCode() : 0);
        result = 31 * result + (isLocked != null ? isLocked.hashCode() : 0);
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        result = 31 * result + (roleLocationId != null ? roleLocationId.hashCode() : 0);
        result = 31 * result + (captcha != null ? captcha.hashCode() : 0);
        result = 31 * result + (userRole != null ? userRole.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SystemUser{" + "systemUserId=" + systemUserId + ", username='" + username + '\'' + ", password='" + password + '\'' + ", loginTime=" + loginTime + ", loginIp='" + loginIp + '\'' + ", isLocked=" + isLocked + ", roleId=" + roleId + ", roleLocationId=" + roleLocationId + ", captcha='" + captcha + '\'' + ", userRole=" + userRole + '}';
    }
}
