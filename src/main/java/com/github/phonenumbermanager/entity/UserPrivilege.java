package com.github.phonenumbermanager.entity;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户权限实体
 *
 * @author 廿二月的天
 */
public class UserPrivilege implements Serializable {
    private static final long serialVersionUID = 4308123257401857501L;
    private Long id;
    private String name;
    private String constraintAuth;
    private String uri;
    private Long parentId;
    private String iconName;
    private Integer orders = 0;
    private Boolean display;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer level;
    private List<UserRole> userRoles;
    private Set<UserPrivilege> subUserPrivileges;

    public UserPrivilege() {
    }

    public UserPrivilege(Long id, String name, String constraintAuth, String uri, Long parentId, String iconName, Integer orders, Boolean display, Timestamp createTime, Timestamp updateTime, Integer level, List<UserRole> userRoles, Set<UserPrivilege> subUserPrivileges) {
        this.id = id;
        this.name = name;
        this.constraintAuth = constraintAuth;
        this.uri = uri;
        this.parentId = parentId;
        this.iconName = iconName;
        this.orders = orders;
        this.display = display;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.level = level;
        this.userRoles = userRoles;
        this.subUserPrivileges = subUserPrivileges;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<UserPrivilege> getSubUserPrivileges() {
        return subUserPrivileges;
    }

    public void setSubUserPrivileges(Set<UserPrivilege> subUserPrivileges) {
        this.subUserPrivileges = subUserPrivileges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPrivilege that = (UserPrivilege) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(constraintAuth, that.constraintAuth)) {
            return false;
        }
        if (!Objects.equals(uri, that.uri)) {
            return false;
        }
        if (!Objects.equals(parentId, that.parentId)) {
            return false;
        }
        if (!Objects.equals(iconName, that.iconName)) {
            return false;
        }
        if (!Objects.equals(orders, that.orders)) {
            return false;
        }
        if (!Objects.equals(display, that.display)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, that.updateTime)) {
            return false;
        }
        if (!Objects.equals(level, that.level)) {
            return false;
        }
        if (!Objects.equals(userRoles, that.userRoles)) {
            return false;
        }
        return Objects.equals(subUserPrivileges, that.subUserPrivileges);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (constraintAuth != null ? constraintAuth.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (iconName != null ? iconName.hashCode() : 0);
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        result = 31 * result + (display != null ? display.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (userRoles != null ? userRoles.hashCode() : 0);
        result = 31 * result + (subUserPrivileges != null ? subUserPrivileges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserPrivilege{" + "id=" + id + ", name='" + name + '\'' + ", constraintAuth='" + constraintAuth + '\'' + ", uri='" + uri + '\'' + ", parentId=" + parentId + ", iconName='" + iconName + '\'' + ", orders=" + orders + ", display=" + display + ", createTime=" + createTime + ", updateTime=" + updateTime + ", level=" + level + ", userRoles=" + userRoles + ", subUserPrivileges=" + subUserPrivileges + '}';
    }
}
