package www.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * 街道办事处实体
 *
 * @author 廿二月的天
 */
public class Subdistrict implements Serializable {
    private static final long serialVersionUID = -7676924584213521372L;
    private Long subdistrictId;
    private String subdistrictName;
    private String subdistrictTelephone;
    private List<Community> communities;

    public Subdistrict() {
    }

    public Subdistrict(Long subdistrictId, String subdistrictName, String subdistrictTelephone, List<Community> communities) {
        super();
        this.subdistrictId = subdistrictId;
        this.subdistrictName = subdistrictName;
        this.subdistrictTelephone = subdistrictTelephone;
        this.communities = communities;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public void setSubdistrictId(Long subdistrictId) {
        this.subdistrictId = subdistrictId;
    }

    public String getSubdistrictName() {
        return subdistrictName;
    }

    public void setSubdistrictName(String subdistrictName) {
        this.subdistrictName = subdistrictName;
    }

    public String getSubdistrictTelephone() {
        return subdistrictTelephone;
    }

    public void setSubdistrictTelephone(String subdistrictTelephone) {
        this.subdistrictTelephone = subdistrictTelephone;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    @Override
    public String toString() {
        return "Subdistrict [id=" + subdistrictId + ", subdistrictName=" + subdistrictName + ", subdistrictTelephone=" + subdistrictTelephone
            + ", communities=" + communities + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subdistrict that = (Subdistrict) o;
        return Objects.equals(subdistrictId, that.subdistrictId) &&
            Objects.equals(subdistrictName, that.subdistrictName) &&
            Objects.equals(subdistrictTelephone, that.subdistrictTelephone) &&
            Objects.equals(communities, that.communities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subdistrictId, subdistrictName, subdistrictTelephone, communities);
    }
}
