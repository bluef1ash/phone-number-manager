package www.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 系统用户实体
 *
 * @author 廿二月的天
 */
public class SystemUser implements Serializable {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

        return (systemUserId != null ? systemUserId.equals(that.systemUserId) : that.systemUserId == null) && (username != null ? username.equals(that.username) : that.username == null) && (password != null ? password.equals(that.password) : that.password == null) && (loginTime != null ? loginTime.equals(that.loginTime) : that.loginTime == null) && (loginIp != null ? loginIp.equals(that.loginIp) : that.loginIp == null) && (isLocked != null ? isLocked.equals(that.isLocked) : that.isLocked == null) && (roleId != null ? roleId.equals(that.roleId) : that.roleId == null) && (roleLocationId != null ? roleLocationId.equals(that.roleLocationId) : that.roleLocationId == null) && (captcha != null ? captcha.equals(that.captcha) : that.captcha == null) && (userRole != null ? userRole.equals(that.userRole) : that.userRole == null);
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
