package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * 社区实体
 *
 * @author 廿二月的天
 */
public class Community implements Serializable {
    private static final long serialVersionUID = 632055591097222014L;
    private Long id;
    private String name;
    private String landline;
    private Integer actualNumber;
    private Boolean dormitorySubmitted;
    private Boolean residentSubmitted;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long subdistrictId;
    private List<CommunityResident> communityResidents;
    private Subdistrict subdistrict;

    public Community() {
    }

    public Community(Long id, String name, String landline, Integer actualNumber, Timestamp createTime, Timestamp updateTime, Long subdistrictId, List<CommunityResident> communityResidents, Subdistrict subdistrict) {
        this.id = id;
        this.name = name;
        this.landline = landline;
        this.actualNumber = actualNumber;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.subdistrictId = subdistrictId;
        this.communityResidents = communityResidents;
        this.subdistrict = subdistrict;
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

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public Integer getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    public Boolean getDormitorySubmitted() {
        return dormitorySubmitted;
    }

    public void setDormitorySubmitted(Boolean dormitorySubmitted) {
        this.dormitorySubmitted = dormitorySubmitted;
    }

    public Boolean getResidentSubmitted() {
        return residentSubmitted;
    }

    public void setResidentSubmitted(Boolean residentSubmitted) {
        this.residentSubmitted = residentSubmitted;
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

    public Long getSubdistrictId() {
        return subdistrictId;
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

        if (!Objects.equals(id, community.id)) {
            return false;
        }
        if (!Objects.equals(name, community.name)) {
            return false;
        }
        if (!Objects.equals(landline, community.landline)) {
            return false;
        }
        if (!Objects.equals(actualNumber, community.actualNumber)) {
            return false;
        }
        if (!Objects.equals(dormitorySubmitted, community.dormitorySubmitted)) {
            return false;
        }
        if (!Objects.equals(residentSubmitted, community.residentSubmitted)) {
            return false;
        }
        if (!Objects.equals(createTime, community.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, community.updateTime)) {
            return false;
        }
        if (!Objects.equals(subdistrictId, community.subdistrictId)) {
            return false;
        }
        if (!Objects.equals(communityResidents, community.communityResidents)) {
            return false;
        }
        return Objects.equals(subdistrict, community.subdistrict);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (landline != null ? landline.hashCode() : 0);
        result = 31 * result + (actualNumber != null ? actualNumber.hashCode() : 0);
        result = 31 * result + (dormitorySubmitted != null ? dormitorySubmitted.hashCode() : 0);
        result = 31 * result + (residentSubmitted != null ? residentSubmitted.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (subdistrictId != null ? subdistrictId.hashCode() : 0);
        result = 31 * result + (communityResidents != null ? communityResidents.hashCode() : 0);
        result = 31 * result + (subdistrict != null ? subdistrict.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Community{" + "id=" + id + ", name='" + name + '\'' + ", landline='" + landline + '\'' + ", actualNumber=" + actualNumber + ", dormitorySubmitted=" + dormitorySubmitted + ", residentSubmitted=" + residentSubmitted + ", createTime=" + createTime + ", updateTime=" + updateTime + ", subdistrictId=" + subdistrictId + ", communityResidents=" + communityResidents + ", subdistrict=" + subdistrict + '}';
    }
}
