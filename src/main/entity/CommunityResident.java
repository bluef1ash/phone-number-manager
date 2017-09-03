package main.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

/**
 * 社区居民实体
 */
public class CommunityResident implements Serializable {
    private static final long serialVersionUID = -5117708878202596711L;
    private Integer communityResidentId;
    private Integer indexId;
    private String communityResidentName;
    private String communityResidentAddress;
    private String communityResidentPhones;
    private String communityResidentSubcontractor;
    private Timestamp communityResidentEditTime;
    private Integer communityId;
    private Integer[] communityIds;
    private Community community;
    private String communityResidentPhone1;
    private String communityResidentPhone2;
    private String communityResidentPhone3;

    public CommunityResident() {
    }

    public CommunityResident(Integer communityResidentId, Integer indexId, String communityResidentName, String communityResidentAddress, String communityResidentPhones, String communityResidentSubcontractor, Timestamp communityResidentEditTime, Integer communityId, Integer[] communityIds, Community community, String communityResidentPhone1, String communityResidentPhone2, String communityResidentPhone3) {
        this.communityResidentId = communityResidentId;
        this.indexId = indexId;
        this.communityResidentName = communityResidentName;
        this.communityResidentAddress = communityResidentAddress;
        this.communityResidentPhones = communityResidentPhones;
        this.communityResidentSubcontractor = communityResidentSubcontractor;
        this.communityResidentEditTime = communityResidentEditTime;
        this.communityId = communityId;
        this.communityIds = communityIds;
        this.community = community;
        this.communityResidentPhone1 = communityResidentPhone1;
        this.communityResidentPhone2 = communityResidentPhone2;
        this.communityResidentPhone3 = communityResidentPhone3;
    }

    public Integer getCommunityResidentId() {
        return communityResidentId;
    }

    public void setCommunityResidentId(Integer communityResidentId) {
        this.communityResidentId = communityResidentId;
    }

    public Integer getIndexId() {
        return indexId;
    }

    public void setIndexId(Integer indexId) {
        this.indexId = indexId;
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

    public String getCommunityResidentSubcontractor() {
        return communityResidentSubcontractor;
    }

    public void setCommunityResidentSubcontractor(String communityResidentSubcontractor) {
        this.communityResidentSubcontractor = communityResidentSubcontractor;
    }

    public Timestamp getCommunityResidentEditTime() {
        return communityResidentEditTime;
    }

    public void setCommunityResidentEditTime(Timestamp communityResidentEditTime) {
        this.communityResidentEditTime = communityResidentEditTime;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public Integer[] getCommunityIds() {
        return communityIds;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public void setCommunityIds(Integer[] communityIds) {
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
    public String toString() {
        return "CommunityResident{" +
            "communityResidentId=" + communityResidentId +
            ", indexId=" + indexId +
            ", communityResidentName='" + communityResidentName + '\'' +
            ", communityResidentAddress='" + communityResidentAddress + '\'' +
            ", communityResidentPhones='" + communityResidentPhones + '\'' +
            ", communityResidentSubcontractor='" + communityResidentSubcontractor + '\'' +
            ", communityResidentEditTime=" + communityResidentEditTime +
            ", communityId=" + communityId +
            ", communityIds=" + Arrays.toString(communityIds) +
            ", community=" + community +
            ", communityResidentPhone1='" + communityResidentPhone1 + '\'' +
            ", communityResidentPhone2='" + communityResidentPhone2 + '\'' +
            ", communityResidentPhone3='" + communityResidentPhone3 + '\'' +
            '}';
    }
}
