package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

/**
 * 用户角色实体
 *
 * @author 廿二月的天
 */
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1243539279689395722L;
    private Long id;
    private String name;
    private String description;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long parentId;
    private Set<SystemUser> systemUsers;
    private Set<UserPrivilege> userPrivileges;

    public UserRole(Long id, String name, String description, Timestamp createTime, Timestamp updateTime, Long parentId, Set<SystemUser> systemUsers, Set<UserPrivilege> userPrivileges) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.parentId = parentId;
        this.systemUsers = systemUsers;
        this.userPrivileges = userPrivileges;
    }

    public UserRole() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

        if (!Objects.equals(id, userRole.id)) {
            return false;
        }
        if (!Objects.equals(name, userRole.name)) {
            return false;
        }
        if (!Objects.equals(description, userRole.description)) {
            return false;
        }
        if (!Objects.equals(createTime, userRole.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, userRole.updateTime)) {
            return false;
        }
        if (!Objects.equals(parentId, userRole.parentId)) {
            return false;
        }
        if (!Objects.equals(systemUsers, userRole.systemUsers)) {
            return false;
        }
        return Objects.equals(userPrivileges, userRole.userPrivileges);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (systemUsers != null ? systemUsers.hashCode() : 0);
        result = 31 * result + (userPrivileges != null ? userPrivileges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRole{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + ", parentId=" + parentId + ", systemUsers=" + systemUsers + ", userPrivileges=" + userPrivileges + '}';
    }
}
