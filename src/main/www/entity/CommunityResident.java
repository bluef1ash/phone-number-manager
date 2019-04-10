package www.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

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
    private Timestamp editTime;
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

    public CommunityResident(Long communityResidentId, Integer indexId, String subdistrictName, String communityName, String communityResidentName, String communityResidentAddress, String communityResidentPhones, Timestamp editTime, Long subcontractorId, Long communityId, Subcontractor subcontractor, String subcontractorName, List<Long> communityIds, Community community, String communityResidentPhone1, String communityResidentPhone2, String communityResidentPhone3) {
        this.communityResidentId = communityResidentId;
        this.indexId = indexId;
        this.subdistrictName = subdistrictName;
        this.communityName = communityName;
        this.communityResidentName = communityResidentName;
        this.communityResidentAddress = communityResidentAddress;
        this.communityResidentPhones = communityResidentPhones;
        this.editTime = editTime;
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

    public Timestamp getEditTime() {
        return editTime;
    }

    public void setEditTime(Timestamp editTime) {
        this.editTime = editTime;
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

        if (!Objects.equals(communityResidentId, that.communityResidentId)) {
            return false;
        }
        if (!Objects.equals(indexId, that.indexId)) {
            return false;
        }
        if (!Objects.equals(subdistrictName, that.subdistrictName)) {
            return false;
        }
        if (!Objects.equals(communityName, that.communityName)) {
            return false;
        }
        if (!Objects.equals(communityResidentName, that.communityResidentName)) {
            return false;
        }
        if (!Objects.equals(communityResidentAddress, that.communityResidentAddress)) {
            return false;
        }
        if (!Objects.equals(communityResidentPhones, that.communityResidentPhones)) {
            return false;
        }
        if (!Objects.equals(editTime, that.editTime)) {
            return false;
        }
        if (!Objects.equals(subcontractorId, that.subcontractorId)) {
            return false;
        }
        if (!Objects.equals(communityId, that.communityId)) {
            return false;
        }
        if (!Objects.equals(subcontractor, that.subcontractor)) {
            return false;
        }
        if (!Objects.equals(subcontractorName, that.subcontractorName)) {
            return false;
        }
        if (!Objects.equals(communityIds, that.communityIds)) {
            return false;
        }
        if (!Objects.equals(community, that.community)) {
            return false;
        }
        if (!Objects.equals(communityResidentPhone1, that.communityResidentPhone1)) {
            return false;
        }
        if (!Objects.equals(communityResidentPhone2, that.communityResidentPhone2)) {
            return false;
        }
        return Objects.equals(communityResidentPhone3, that.communityResidentPhone3);
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
        result = 31 * result + (editTime != null ? editTime.hashCode() : 0);
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
        return "CommunityResident{" + "communityResidentId=" + communityResidentId + ", indexId=" + indexId + ", subdistrictName='" + subdistrictName + '\'' + ", communityName='" + communityName + '\'' + ", communityResidentName='" + communityResidentName + '\'' + ", communityResidentAddress='" + communityResidentAddress + '\'' + ", communityResidentPhones='" + communityResidentPhones + '\'' + ", editTime=" + editTime + ", subcontractorId=" + subcontractorId + ", communityId=" + communityId + ", subcontractor=" + subcontractor + ", subcontractorName='" + subcontractorName + '\'' + ", communityIds=" + communityIds + ", community=" + community + ", communityResidentPhone1='" + communityResidentPhone1 + '\'' + ", communityResidentPhone2='" + communityResidentPhone2 + '\'' + ", communityResidentPhone3='" + communityResidentPhone3 + '\'' + '}';
    }
}
