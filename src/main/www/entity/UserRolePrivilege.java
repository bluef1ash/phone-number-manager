package www.entity;

import java.io.Serializable;

/**
 * 用户角色与用户权限中间实体
 *
 * @author 廿二月的天
 */
public class UserRolePrivilege implements Serializable {
    private static final long serialVersionUID = -2765782497604371615L;
    private Long roleId;
    private Long privilegeId;

    public UserRolePrivilege() {
    }

    public UserRolePrivilege(Long roleId, Long privilegeId) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRolePrivilege that = (UserRolePrivilege) o;

        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) {
            return false;
        }
        return privilegeId != null ? privilegeId.equals(that.privilegeId) : that.privilegeId == null;
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (privilegeId != null ? privilegeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRolePrivilege [roleId=" + roleId + ", privilegeId=" + privilegeId + "]";
    }
}
