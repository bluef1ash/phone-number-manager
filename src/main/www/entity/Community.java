package www.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 社区实体
 *
 * @author 廿二月的天
 */
public class Community implements Serializable {
    private static final long serialVersionUID = 632055591097222014L;
    private Long communityId;
    private String communityName;
    private String communityTelephone;
    private Integer actualNumber;
    private Long subdistrictId;
    private List<CommunityResident> communityResidents;
    private Subdistrict subdistrict;

    public Community() {
    }

    public Community(Long communityId, String communityName, String communityTelephone, Integer actualNumber, Long subdistrictId, List<CommunityResident> communityResidents, Subdistrict subdistrict) {
        this.communityId = communityId;
        this.communityName = communityName;
        this.communityTelephone = communityTelephone;
        this.actualNumber = actualNumber;
        this.subdistrictId = subdistrictId;
        this.communityResidents = communityResidents;
        this.subdistrict = subdistrict;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityTelephone() {
        return communityTelephone;
    }

    public void setCommunityTelephone(String communityTelephone) {
        this.communityTelephone = communityTelephone;
    }

    public Long getSubdistrictId() {
        return subdistrictId;
    }

    public Integer getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    public void setSubdistrictId(Long subdistrictId) {
        this.subdistrictId = subdistrictId;
    }

    public List<CommunityResident> getCommunityResidents() {
        return communityResidents;
    }

    public void setCommunityResidents(List<CommunityResident> communityResidents) {
        this.communityResidents = communityResidents;
    }

    public Subdistrict getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(Subdistrict subdistrict) {
        this.subdistrict = subdistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Community community = (Community) o;

        if (communityId != null ? !communityId.equals(community.communityId) : community.communityId != null) {
            return false;
        }
        if (communityName != null ? !communityName.equals(community.communityName) : community.communityName != null) {
            return false;
        }
        if (communityTelephone != null ? !communityTelephone.equals(community.communityTelephone) : community.communityTelephone != null) {
            return false;
        }
        if (actualNumber != null ? !actualNumber.equals(community.actualNumber) : community.actualNumber != null) {
            return false;
        }
        if (subdistrictId != null ? !subdistrictId.equals(community.subdistrictId) : community.subdistrictId != null) {
            return false;
        }
        if (communityResidents != null ? !communityResidents.equals(community.communityResidents) : community.communityResidents != null) {
            return false;
        }
        return subdistrict != null ? subdistrict.equals(community.subdistrict) : community.subdistrict == null;
    }

    @Override
    public int hashCode() {
        int result = communityId != null ? communityId.hashCode() : 0;
        result = 31 * result + (communityName != null ? communityName.hashCode() : 0);
        result = 31 * result + (communityTelephone != null ? communityTelephone.hashCode() : 0);
        result = 31 * result + (actualNumber != null ? actualNumber.hashCode() : 0);
        result = 31 * result + (subdistrictId != null ? subdistrictId.hashCode() : 0);
        result = 31 * result + (communityResidents != null ? communityResidents.hashCode() : 0);
        result = 31 * result + (subdistrict != null ? subdistrict.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Community [communityId=" + communityId + ", communityName=" + communityName + ", communityTelephone=" + communityTelephone + ", actualNumber=" + actualNumber + ", subdistrictId=" + subdistrictId + ", communityResidents=" + communityResidents + ", subdistrict=" + subdistrict + "]";
    }
}
