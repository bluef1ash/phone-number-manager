package main.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
/**
 * 用户权限实体
 *
 */
public class UserPrivilege implements Serializable {
	private static final long serialVersionUID = 4308123257401857501L;
	private Integer privilegeId;
	@NotNull(message="{userPrivilege.privilegeName.isNull}")
	private String privilegeName;
	@NotNull(message="{userPrivilege.constraintAuth.isNull}")
	private String constraintAuth;
	@NotNull(message="{userPrivilege.uri.isNull}")
	private String uri;
	@NotNull(message="{userPrivilege.higherPrivilege.isNull}")
	private Integer higherPrivilege;
	@NotNull(message="{userPrivilege.iconName.isNull}")
	private String iconName;
	private Integer orders = 0;
	private Integer isDisplay;
	private List<UserRole> userRoles;
	private List<UserPrivilege> subUserPrivileges;
	public UserPrivilege() {}
	public UserPrivilege(Integer privilegeId, String privilegeName, String constraintAuth,
			String uri, Integer higherPrivilege, String iconName, Integer orders, Integer isDisplay, List<UserRole> userRoles, List<UserPrivilege> subUserPrivileges) {
		this.privilegeId = privilegeId;
		this.privilegeName = privilegeName;
		this.constraintAuth = constraintAuth;
		this.uri = uri;
		this.higherPrivilege = higherPrivilege;
		this.iconName = iconName;
		this.orders = orders;
		this.isDisplay = isDisplay;
		this.userRoles = userRoles;
		this.subUserPrivileges = subUserPrivileges;
	}
	public Integer getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(Integer privilegeId) {
		this.privilegeId = privilegeId;
	}
	public String getPrivilegeName() {
		return privilegeName;
	}
	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}
	public String getConstraintAuth() {
		return constraintAuth;
	}
	public void setConstraintAuth(String constraintAuth) {
		this.constraintAuth = constraintAuth;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Integer getHigherPrivilege() {
		return higherPrivilege;
	}
	public void setHigherPrivilege(Integer higherPrivilege) {
		this.higherPrivilege = higherPrivilege;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public Integer getOrders() {
		return orders;
	}
	public void setOrders(Integer orders) {
		this.orders = orders;
	}
	public Integer getIsDisplay() {
		return isDisplay;
	}
	public void setIsDisplay(Integer isDisplay) {
		this.isDisplay = isDisplay;
	}
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	public List<UserPrivilege> getSubUserPrivileges() {
		return subUserPrivileges;
	}
	public void setSubUserPrivileges(List<UserPrivilege> subUserPrivileges) {
		this.subUserPrivileges = subUserPrivileges;
	}
	@Override
	public String toString() {
		return "UserPrivilege [privilegeId=" + privilegeId + ", privilegeName=" + privilegeName
				+ ", constraintAuth=" + constraintAuth + ", uri="
				+ uri + ", higherPrivilege=" + higherPrivilege + ", iconName=" + iconName + ", orders=" + orders
				+ ", isDisplay=" + isDisplay + ", userRoles=" + userRoles + ", subUserPrivileges=" + subUserPrivileges
				+ "]";
	}
}
