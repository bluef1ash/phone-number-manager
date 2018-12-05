package www.entity;

import java.io.Serializable;

/**
 * 社区分包人实体
 *
 * @author 廿二月的天
 */
public class Subcontractor implements Serializable {
    private static final long serialVersionUID = -5689525073729148400L;
    private Long subcontractorId;
    private String name;
    private String telephone;
    private Long communityId;
    private Community community;

    public Subcontractor() {
    }

    public Subcontractor(Long subcontractorId, String name, String telephone, Long communityId, Community community) {
        this.subcontractorId = subcontractorId;
        this.name = name;
        this.telephone = telephone;
        this.communityId = communityId;
        this.community = community;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subcontractor that = (Subcontractor) o;

        if (subcontractorId != null ? !subcontractorId.equals(that.subcontractorId) : that.subcontractorId != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (telephone != null ? !telephone.equals(that.telephone) : that.telephone != null) {
            return false;
        }
        if (communityId != null ? !communityId.equals(that.communityId) : that.communityId != null) {
            return false;
        }
        return community != null ? community.equals(that.community) : that.community == null;
    }

    @Override
    public int hashCode() {
        int result = subcontractorId != null ? subcontractorId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (communityId != null ? communityId.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Subcontractor{" + "subcontractorId=" + subcontractorId + ", name='" + name + '\'' + ", telephone='" + telephone + '\'' + ", communityId=" + communityId + ", community=" + community + '}';
    }
}
