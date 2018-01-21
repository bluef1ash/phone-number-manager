package www.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 用户权限实体
 *
 * @author 廿二月的天
 */
public class UserPrivilege implements Serializable {
    private static final long serialVersionUID = 4308123257401857501L;
    private Integer privilegeId;
    private String privilegeName;
    private String constraintAuth;
    private String uri;
    private Integer higherPrivilege;
    private String iconName;
    private Integer orders = 0;
    private Integer isDisplay;
    private List<UserRole> userRoles;
    private List<UserPrivilege> subUserPrivileges;

    public UserPrivilege() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPrivilege that = (UserPrivilege) o;

        return (privilegeId != null ? privilegeId.equals(that.privilegeId) : that.privilegeId == null) && (privilegeName != null ? privilegeName.equals(that.privilegeName) : that.privilegeName == null) && (constraintAuth != null ? constraintAuth.equals(that.constraintAuth) : that.constraintAuth == null) && (uri != null ? uri.equals(that.uri) : that.uri == null) && (higherPrivilege != null ? higherPrivilege.equals(that.higherPrivilege) : that.higherPrivilege == null) && (iconName != null ? iconName.equals(that.iconName) : that.iconName == null) && (orders != null ? orders.equals(that.orders) : that.orders == null) && (isDisplay != null ? isDisplay.equals(that.isDisplay) : that.isDisplay == null) && (userRoles != null ? userRoles.equals(that.userRoles) : that.userRoles == null) && (subUserPrivileges != null ? subUserPrivileges.equals(that.subUserPrivileges) : that.subUserPrivileges == null);
    }

    @Override
    public int hashCode() {
        int result = privilegeId != null ? privilegeId.hashCode() : 0;
        result = 31 * result + (privilegeName != null ? privilegeName.hashCode() : 0);
        result = 31 * result + (constraintAuth != null ? constraintAuth.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (higherPrivilege != null ? higherPrivilege.hashCode() : 0);
        result = 31 * result + (iconName != null ? iconName.hashCode() : 0);
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        result = 31 * result + (isDisplay != null ? isDisplay.hashCode() : 0);
        result = 31 * result + (userRoles != null ? userRoles.hashCode() : 0);
        result = 31 * result + (subUserPrivileges != null ? subUserPrivileges.hashCode() : 0);
        return result;
    }
}
