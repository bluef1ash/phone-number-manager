package www.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 社区居民实体
 *
 * @author 廿二月的天
 */
public class CommunityResident implements Serializable {
    private static final long serialVersionUID = -5117708878202596711L;
    private Long communityResidentId;
    private Integer indexId;
    private String subdistrictName;
    private String communityName;
    private String communityResidentName;
    private String communityResidentAddress;
    private String communityResidentPhones;
    private Timestamp communityResidentEditTime;
    private Long subcontractorId;
    private Long communityId;
    private Subcontractor subcontractor;
    private String subcontractorName;
    private List<Long> communityIds;
    private Community community;
    private String communityResidentPhone1;
    private String communityResidentPhone2;
    private String communityResidentPhone3;

    public CommunityResident() {
    }

    public CommunityResident(Long communityResidentId, Integer indexId, String subdistrictName, String communityName, String communityResidentName, String communityResidentAddress, String communityResidentPhones, Timestamp communityResidentEditTime, Long subcontractorId, Long communityId, Subcontractor subcontractor, String subcontractorName, List<Long> communityIds, Community community, String communityResidentPhone1, String communityResidentPhone2, String communityResidentPhone3) {
        this.communityResidentId = communityResidentId;
        this.indexId = indexId;
        this.subdistrictName = subdistrictName;
        this.communityName = communityName;
        this.communityResidentName = communityResidentName;
        this.communityResidentAddress = communityResidentAddress;
        this.communityResidentPhones = communityResidentPhones;
        this.communityResidentEditTime = communityResidentEditTime;
        this.subcontractorId = subcontractorId;
        this.communityId = communityId;
        this.subcontractor = subcontractor;
        this.subcontractorName = subcontractorName;
        this.communityIds = communityIds;
        this.community = community;
        this.communityResidentPhone1 = communityResidentPhone1;
        this.communityResidentPhone2 = communityResidentPhone2;
        this.communityResidentPhone3 = communityResidentPhone3;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getCommunityResidentId() {
        return communityResidentId;
    }

    public void setCommunityResidentId(Long communityResidentId) {
        this.communityResidentId = communityResidentId;
    }

    public Integer getIndexId() {
        return indexId;
    }

    public void setIndexId(Integer indexId) {
        this.indexId = indexId;
    }

    public String getSubdistrictName() {
        return subdistrictName;
    }

    public void setSubdistrictName(String subdistrictName) {
        this.subdistrictName = subdistrictName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityResidentName() {
        return communityResidentName;
    }

    public void setCommunityResidentName(String communityResidentName) {
        this.communityResidentName = communityResidentName;
    }

    public String getCommunityResidentAddress() {
        return communityResidentAddress;
    }

    public void setCommunityResidentAddress(String communityResidentAddress) {
        this.communityResidentAddress = communityResidentAddress;
    }

    public String getCommunityResidentPhones() {
        return communityResidentPhones;
    }

    public void setCommunityResidentPhones(String communityResidentPhones) {
        this.communityResidentPhones = communityResidentPhones;
    }

    public Timestamp getCommunityResidentEditTime() {
        return communityResidentEditTime;
    }

    public void setCommunityResidentEditTime(Timestamp communityResidentEditTime) {
        this.communityResidentEditTime = communityResidentEditTime;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Subcontractor getSubcontractor() {
        return subcontractor;
    }

    public void setSubcontractor(Subcontractor subcontractor) {
        this.subcontractor = subcontractor;
    }

    public String getSubcontractorName() {
        return subcontractorName;
    }

    public void setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getCommunityResidentPhone1() {
        return communityResidentPhone1;
    }

    public void setCommunityResidentPhone1(String communityResidentPhone1) {
        this.communityResidentPhone1 = communityResidentPhone1;
    }

    public String getCommunityResidentPhone2() {
        return communityResidentPhone2;
    }

    public void setCommunityResidentPhone2(String communityResidentPhone2) {
        this.communityResidentPhone2 = communityResidentPhone2;
    }

    public String getCommunityResidentPhone3() {
        return communityResidentPhone3;
    }

    public void setCommunityResidentPhone3(String communityResidentPhone3) {
        this.communityResidentPhone3 = communityResidentPhone3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommunityResident that = (CommunityResident) o;

        if (communityResidentId != null ? !communityResidentId.equals(that.communityResidentId) : that.communityResidentId != null) {
            return false;
        }
        if (indexId != null ? !indexId.equals(that.indexId) : that.indexId != null) {
            return false;
        }
        if (subdistrictName != null ? !subdistrictName.equals(that.subdistrictName) : that.subdistrictName != null) {
            return false;
        }
        if (communityName != null ? !communityName.equals(that.communityName) : that.communityName != null) {
            return false;
        }
        if (communityResidentName != null ? !communityResidentName.equals(that.communityResidentName) : that.communityResidentName != null) {
            return false;
        }
        if (communityResidentAddress != null ? !communityResidentAddress.equals(that.communityResidentAddress) : that.communityResidentAddress != null) {
            return false;
        }
        if (communityResidentPhones != null ? !communityResidentPhones.equals(that.communityResidentPhones) : that.communityResidentPhones != null) {
            return false;
        }
        if (communityResidentEditTime != null ? !communityResidentEditTime.equals(that.communityResidentEditTime) : that.communityResidentEditTime != null) {
            return false;
        }
        if (subcontractorId != null ? !subcontractorId.equals(that.subcontractorId) : that.subcontractorId != null) {
            return false;
        }
        if (communityId != null ? !communityId.equals(that.communityId) : that.communityId != null) {
            return false;
        }
        if (subcontractor != null ? !subcontractor.equals(that.subcontractor) : that.subcontractor != null) {
            return false;
        }
        if (subcontractorName != null ? !subcontractorName.equals(that.subcontractorName) : that.subcontractorName != null) {
            return false;
        }
        if (communityIds != null ? !communityIds.equals(that.communityIds) : that.communityIds != null) {
            return false;
        }
        if (community != null ? !community.equals(that.community) : that.community != null) {
            return false;
        }
        if (communityResidentPhone1 != null ? !communityResidentPhone1.equals(that.communityResidentPhone1) : that.communityResidentPhone1 != null) {
            return false;
        }
        if (communityResidentPhone2 != null ? !communityResidentPhone2.equals(that.communityResidentPhone2) : that.communityResidentPhone2 != null) {
            return false;
        }
        return communityResidentPhone3 != null ? communityResidentPhone3.equals(that.communityResidentPhone3) : that.communityResidentPhone3 == null;
    }

    @Override
    public int hashCode() {
        int result = communityResidentId != null ? communityResidentId.hashCode() : 0;
        result = 31 * result + (indexId != null ? indexId.hashCode() : 0);
        result = 31 * result + (subdistrictName != null ? subdistrictName.hashCode() : 0);
        result = 31 * result + (communityName != null ? communityName.hashCode() : 0);
        result = 31 * result + (communityResidentName != null ? communityResidentName.hashCode() : 0);
        result = 31 * result + (communityResidentAddress != null ? communityResidentAddress.hashCode() : 0);
        result = 31 * result + (communityResidentPhones != null ? communityResidentPhones.hashCode() : 0);
        result = 31 * result + (communityResidentEditTime != null ? communityResidentEditTime.hashCode() : 0);
        result = 31 * result + (subcontractorId != null ? subcontractorId.hashCode() : 0);
        result = 31 * result + (communityId != null ? communityId.hashCode() : 0);
        result = 31 * result + (subcontractor != null ? subcontractor.hashCode() : 0);
        result = 31 * result + (subcontractorName != null ? subcontractorName.hashCode() : 0);
        result = 31 * result + (communityIds != null ? communityIds.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        result = 31 * result + (communityResidentPhone1 != null ? communityResidentPhone1.hashCode() : 0);
        result = 31 * result + (communityResidentPhone2 != null ? communityResidentPhone2.hashCode() : 0);
        result = 31 * result + (communityResidentPhone3 != null ? communityResidentPhone3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommunityResident{" + "communityResidentId=" + communityResidentId + ", indexId=" + indexId + ", subdistrictName='" + subdistrictName + '\'' + ", communityName='" + communityName + '\'' + ", communityResidentName='" + communityResidentName + '\'' + ", communityResidentAddress='" + communityResidentAddress + '\'' + ", communityResidentPhones='" + communityResidentPhones + '\'' + ", communityResidentEditTime=" + communityResidentEditTime + ", subcontractorId=" + subcontractorId + ", communityId=" + communityId + ", subcontractor=" + subcontractor + ", subcontractorName='" + subcontractorName + '\'' + ", communityIds=" + communityIds + ", community=" + community + ", communityResidentPhone1='" + communityResidentPhone1 + '\'' + ", communityResidentPhone2='" + communityResidentPhone2 + '\'' + ", communityResidentPhone3='" + communityResidentPhone3 + '\'' + '}';
    }
}
