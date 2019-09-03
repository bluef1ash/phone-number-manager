package com.github.phonenumbermanager.entity;

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
    private Long id;
    private Integer indexId;
    private String subdistrictName;
    private String communityName;
    private String name;
    private String address;
    private String phones;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long subcontractorId;
    private Long communityId;
    private Subcontractor subcontractor;
    private String subcontractorName;
    private List<Long> communityIds;
    private Community community;
    private String phone1;
    private String phone2;
    private String phone3;

    public CommunityResident() {
    }

    public CommunityResident(Long id, Integer indexId, String subdistrictName, String communityName, String name, String address, String phones, Timestamp createTime, Timestamp updateTime, Long subcontractorId, Long communityId, Subcontractor subcontractor, String subcontractorName, List<Long> communityIds, Community community, String phone1, String phone2, String phone3) {
        this.id = id;
        this.indexId = indexId;
        this.subdistrictName = subdistrictName;
        this.communityName = communityName;
        this.name = name;
        this.address = address;
        this.phones = phones;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.subcontractorId = subcontractorId;
        this.communityId = communityId;
        this.subcontractor = subcontractor;
        this.subcontractorName = subcontractorName;
        this.communityIds = communityIds;
        this.community = community;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
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

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
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

        if (!Objects.equals(id, that.id)) {
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
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(address, that.address)) {
            return false;
        }
        if (!Objects.equals(phones, that.phones)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, that.updateTime)) {
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
        if (!Objects.equals(phone1, that.phone1)) {
            return false;
        }
        if (!Objects.equals(phone2, that.phone2)) {
            return false;
        }
        return Objects.equals(phone3, that.phone3);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (indexId != null ? indexId.hashCode() : 0);
        result = 31 * result + (subdistrictName != null ? subdistrictName.hashCode() : 0);
        result = 31 * result + (communityName != null ? communityName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phones != null ? phones.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (subcontractorId != null ? subcontractorId.hashCode() : 0);
        result = 31 * result + (communityId != null ? communityId.hashCode() : 0);
        result = 31 * result + (subcontractor != null ? subcontractor.hashCode() : 0);
        result = 31 * result + (subcontractorName != null ? subcontractorName.hashCode() : 0);
        result = 31 * result + (communityIds != null ? communityIds.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        result = 31 * result + (phone1 != null ? phone1.hashCode() : 0);
        result = 31 * result + (phone2 != null ? phone2.hashCode() : 0);
        result = 31 * result + (phone3 != null ? phone3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommunityResident{" + "id=" + id + ", indexId=" + indexId + ", subdistrictName='" + subdistrictName + '\'' + ", communityName='" + communityName + '\'' + ", name='" + name + '\'' + ", address='" + address + '\'' + ", phones='" + phones + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + ", subcontractorId=" + subcontractorId + ", communityId=" + communityId + ", subcontractor=" + subcontractor + ", subcontractorName='" + subcontractorName + '\'' + ", communityIds=" + communityIds + ", community=" + community + ", phone1='" + phone1 + '\'' + ", phone2='" + phone2 + '\'' + ", phone3='" + phone3 + '\'' + '}';
    }
}
