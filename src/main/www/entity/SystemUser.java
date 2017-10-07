package www.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;
/**
 * 系统用户实体
 *
 */
public class SystemUser implements Serializable {
	private static final long serialVersionUID = -5035917619026010434L;
	private Integer systemUserId;
	@NotNull(message="{systemUser.username.isNull}")
	private String username;
	@NotNull(message="{systemUser.password.isNull}")
	private String password;
	private Timestamp loginTime = new Timestamp(1000);
	private String loginIp = "";
	private Integer isLocked;
	private Integer roleId;
	private Integer roleLocationId;
	private UserRole userRole;
	public SystemUser() {}
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
}
