package www.entity;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;
/**
 * 用户角色实体
 *
 */
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1243539279689395722L;
	private Integer roleId;
	private String roleName;
	private String roleDescription;
	private Integer higherRole;
	private Set<SystemUser> systemUsers;
	private Set<UserPrivilege> userPrivileges;
	public UserRole() {}
	public UserRole(Integer roleId, String roleName, String roleDescription, Integer higherRole, Set<SystemUser> systemUsers) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
		this.higherRole = higherRole;
		this.systemUsers = systemUsers;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	public Integer getHigherRole() {
		return higherRole;
	}
	public void setHigherRole(Integer higherRole) {
		this.higherRole = higherRole;
	}
	public Set<SystemUser> getSystemUsers() {
		return systemUsers;
	}
	public void setSystemUsers(Set<SystemUser> systemUsers) {
		this.systemUsers = systemUsers;
	}
	public Set<UserPrivilege> getUserPrivileges() {
		return userPrivileges;
	}
	public void setUserPrivileges(Set<UserPrivilege> userPrivileges) {
		this.userPrivileges = userPrivileges;
	}
	@Override
	public String toString() {
		return "UserRole [roleId=" + roleId + ", roleName=" + roleName + ", roleDescription=" + roleDescription
				+ ", higherRole=" + higherRole + ", systemUsers=" + systemUsers + ", userPrivileges=" + userPrivileges + "]";
	}
}
