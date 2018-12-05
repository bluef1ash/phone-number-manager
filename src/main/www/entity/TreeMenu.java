package www.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 树形菜单
 *
 * @author 廿二月的天
 */
public class TreeMenu implements Serializable {
    private static final long serialVersionUID = 8852726242252640057L;
    private Long id;
    private String name;
    private Boolean checked;
    private List<TreeMenu> children;
    private Boolean chkDisabled;
    private Boolean halfCheck;
    private String icon;
    private Boolean nocheck;
    private String target;
    private String url;
    private Long roleLocationId;

    public TreeMenu() {
        super();
    }

    public TreeMenu(Long id, String name, Boolean checked, List<TreeMenu> children, Boolean chkDisabled, Boolean halfCheck, String icon, Boolean nocheck, String target, String url, Long roleLocationId) {
        this.id = id;
        this.name = name;
        this.checked = checked;
        this.children = children;
        this.chkDisabled = chkDisabled;
        this.halfCheck = halfCheck;
        this.icon = icon;
        this.nocheck = nocheck;
        this.target = target;
        this.url = url;
        this.roleLocationId = roleLocationId;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public List<TreeMenu> getChildren() {
        return children;
    }

    public void setChildren(List<TreeMenu> children) {
        this.children = children;
    }

    public Boolean getChkDisabled() {
        return chkDisabled;
    }

    public void setChkDisabled(Boolean chkDisabled) {
        this.chkDisabled = chkDisabled;
    }

    public Boolean getHalfCheck() {
        return halfCheck;
    }

    public void setHalfCheck(Boolean halfCheck) {
        this.halfCheck = halfCheck;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getNocheck() {
        return nocheck;
    }

    public void setNocheck(Boolean nocheck) {
        this.nocheck = nocheck;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getRoleLocationId() {
        return roleLocationId;
    }

    public void setRoleLocationId(Long roleLocationId) {
        this.roleLocationId = roleLocationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TreeMenu treeMenu = (TreeMenu) o;

        if (id != null ? !id.equals(treeMenu.id) : treeMenu.id != null) {
            return false;
        }
        if (name != null ? !name.equals(treeMenu.name) : treeMenu.name != null) {
            return false;
        }
        if (checked != null ? !checked.equals(treeMenu.checked) : treeMenu.checked != null) {
            return false;
        }
        if (children != null ? !children.equals(treeMenu.children) : treeMenu.children != null) {
            return false;
        }
        if (chkDisabled != null ? !chkDisabled.equals(treeMenu.chkDisabled) : treeMenu.chkDisabled != null) {
            return false;
        }
        if (halfCheck != null ? !halfCheck.equals(treeMenu.halfCheck) : treeMenu.halfCheck != null) {
            return false;
        }
        if (icon != null ? !icon.equals(treeMenu.icon) : treeMenu.icon != null) {
            return false;
        }
        if (nocheck != null ? !nocheck.equals(treeMenu.nocheck) : treeMenu.nocheck != null) {
            return false;
        }
        if (target != null ? !target.equals(treeMenu.target) : treeMenu.target != null) {
            return false;
        }
        if (url != null ? !url.equals(treeMenu.url) : treeMenu.url != null) {
            return false;
        }
        return roleLocationId != null ? roleLocationId.equals(treeMenu.roleLocationId) : treeMenu.roleLocationId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (checked != null ? checked.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (chkDisabled != null ? chkDisabled.hashCode() : 0);
        result = 31 * result + (halfCheck != null ? halfCheck.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (nocheck != null ? nocheck.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (roleLocationId != null ? roleLocationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TreeMenu{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", checked=" + checked +
            ", children=" + children +
            ", chkDisabled=" + chkDisabled +
            ", halfCheck=" + halfCheck +
            ", icon='" + icon + '\'' +
            ", nocheck=" + nocheck +
            ", target='" + target + '\'' +
            ", url='" + url + '\'' +
            ", roleLocationId=" + roleLocationId +
            '}';
    }
}
