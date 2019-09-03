package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 用户角色与用户权限中间实体
 *
 * @author 廿二月的天
 */
public class UserRolePrivilege implements Serializable {
    private static final long serialVersionUID = -2765782497604371615L;
    private Long roleId;
    private Long privilegeId;
    private Timestamp createTime;
    private Timestamp updateTime;

    public UserRolePrivilege() {
    }

    public UserRolePrivilege(Long roleId, Long privilegeId, Timestamp createTime, Timestamp updateTime) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
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

        if (!Objects.equals(roleId, that.roleId)) {
            return false;
        }
        if (!Objects.equals(privilegeId, that.privilegeId)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        return Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (privilegeId != null ? privilegeId.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRolePrivilege{" + "roleId=" + roleId + ", privilegeId=" + privilegeId + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
