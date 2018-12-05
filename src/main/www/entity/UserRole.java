package www.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户角色实体
 *
 * @author 廿二月的天
 */
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1243539279689395722L;
    private Long roleId;
    private String roleName;
    private String roleDescription;
    private Long higherRole;
    private Set<SystemUser> systemUsers;
    private Set<UserPrivilege> userPrivileges;

    public UserRole() {
    }

    public UserRole(Long roleId, String roleName, String roleDescription, Long higherRole, Set<SystemUser> systemUsers) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.higherRole = higherRole;
        this.systemUsers = systemUsers;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
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

    public Long getHigherRole() {
        return higherRole;
    }

    public void setHigherRole(Long higherRole) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRole userRole = (UserRole) o;

        if (roleId != null ? !roleId.equals(userRole.roleId) : userRole.roleId != null) {
            return false;
        }
        if (roleName != null ? !roleName.equals(userRole.roleName) : userRole.roleName != null) {
            return false;
        }
        if (roleDescription != null ? !roleDescription.equals(userRole.roleDescription) : userRole.roleDescription != null) {
            return false;
        }
        if (higherRole != null ? !higherRole.equals(userRole.higherRole) : userRole.higherRole != null) {
            return false;
        }
        if (systemUsers != null ? !systemUsers.equals(userRole.systemUsers) : userRole.systemUsers != null) {
            return false;
        }
        return userPrivileges != null ? userPrivileges.equals(userRole.userPrivileges) : userRole.userPrivileges == null;
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        result = 31 * result + (roleDescription != null ? roleDescription.hashCode() : 0);
        result = 31 * result + (higherRole != null ? higherRole.hashCode() : 0);
        result = 31 * result + (systemUsers != null ? systemUsers.hashCode() : 0);
        result = 31 * result + (userPrivileges != null ? userPrivileges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRole [roleId=" + roleId + ", roleName=" + roleName + ", roleDescription=" + roleDescription
            + ", higherRole=" + higherRole + ", systemUsers=" + systemUsers + ", userPrivileges=" + userPrivileges + "]";
    }
}
