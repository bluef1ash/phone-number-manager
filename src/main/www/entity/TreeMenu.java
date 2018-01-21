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
    private Integer id;
    private String name;
    private String href;
    private Boolean spread;
    private String alias;
    private List<TreeMenu> children;

    public TreeMenu() {
        super();
    }

    public TreeMenu(Integer id, String name, String href, Boolean spread, String alias, List<TreeMenu> children) {
        this.id = id;
        this.name = name;
        this.href = href;
        this.spread = spread;
        this.alias = alias;
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Boolean getSpread() {
        return spread;
    }

    public void setSpread(Boolean spread) {
        this.spread = spread;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<TreeMenu> getChildren() {
        return children;
    }

    public void setChildren(List<TreeMenu> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "TreeMenu{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", href='" + href + '\'' +
            ", spread=" + spread +
            ", alias='" + alias + '\'' +
            ", children=" + children +
            '}';
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

        return (id != null ? id.equals(treeMenu.id) : treeMenu.id == null) && (name != null ? name.equals(treeMenu.name) : treeMenu.name == null) && (href != null ? href.equals(treeMenu.href) : treeMenu.href == null) && (spread != null ? spread.equals(treeMenu.spread) : treeMenu.spread == null) && (alias != null ? alias.equals(treeMenu.alias) : treeMenu.alias == null) && (children != null ? children.equals(treeMenu.children) : treeMenu.children == null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (href != null ? href.hashCode() : 0);
        result = 31 * result + (spread != null ? spread.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
