package main.entity;

import java.io.Serializable;
/**
 * 用户角色与用户权限中间实体
 *
 */
public class UserRolePrivilege implements Serializable {
	private static final long serialVersionUID = -2765782497604371615L;
	private Integer roleId;
	private Integer privilegeId;
	public UserRolePrivilege() {}
	public UserRolePrivilege(Integer roleId, Integer privilegeId) {
		this.roleId = roleId;
		this.privilegeId = privilegeId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(Integer privilegeId) {
		this.privilegeId = privilegeId;
	}
	@Override
	public String toString() {
		return "UserRolePrivilege [roleId=" + roleId + ", privilegeId=" + privilegeId + "]";
	}
}