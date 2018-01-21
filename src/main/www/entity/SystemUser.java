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
    private Integer systemUserId;
    private String username;
    private String password;
    private Timestamp loginTime = new Timestamp(1000);
    private String loginIp = "";
    private Integer isLocked;
    private Integer roleId;
    private Integer roleLocationId;
    private UserRole userRole;

    public SystemUser() {
    }

    public SystemUser(Integer systemUserId, String username, String password, Timestamp loginTime, String loginIp,
                      Integer isLocked, Integer roleId, Integer roleLocationId, UserRole userRole) {
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

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getRoleLocationId() {
        return roleLocationId;
    }

    public void setRoleLocationId(Integer roleLocationId) {
        this.roleLocationId = roleLocationId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "SystemUser [systemUserId=" + systemUserId + ", username=" + username + ", password=" + password
            + ", loginTime=" + loginTime + ", loginIp=" + loginIp + ", isLocked=" + isLocked + ", roleId=" + roleId
            + ", roleLocationId=" + roleLocationId + ", userRole=" + userRole + "]";
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

        return (systemUserId != null ? systemUserId.equals(that.systemUserId) : that.systemUserId == null) && (username != null ? username.equals(that.username) : that.username == null) && (password != null ? password.equals(that.password) : that.password == null) && (loginTime != null ? loginTime.equals(that.loginTime) : that.loginTime == null) && (loginIp != null ? loginIp.equals(that.loginIp) : that.loginIp == null) && (isLocked != null ? isLocked.equals(that.isLocked) : that.isLocked == null) && (roleId != null ? roleId.equals(that.roleId) : that.roleId == null) && (roleLocationId != null ? roleLocationId.equals(that.roleLocationId) : that.roleLocationId == null) && (userRole != null ? userRole.equals(that.userRole) : that.userRole == null);
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
        result = 31 * result + (userRole != null ? userRole.hashCode() : 0);
        return result;
    }
}
