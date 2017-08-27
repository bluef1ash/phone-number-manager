package main.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.validation.constraints.NotNull;
/**
 * 社区居民实体
 *
 */
public class CommunityResident implements Serializable {
	private static final long serialVersionUID = -5117708878202596711L;
	private Integer communityResidentId;
	@NotNull(message="{communityResident.communityResidentName.isNull}")
	private String communityResidentName;
	@NotNull(message="{communityResident.communityResidentAddress.isNull}")
	private String communityResidentAddress;
	//@NotNull(message="{communityResident.communityResidentPhones.isNull}")
	private String communityResidentPhones;
	@NotNull(message="{communityResident.communityResidentSubcontractor.isNull}")
	private String communityResidentSubcontractor;
	private Timestamp communityResidentEditTime;
	private Integer communityId;
	private Integer[] communityIds;
	private Community community;
	public CommunityResident() {}
	public CommunityResident(Integer communityResidentId, String communityResidentName, String communityResidentAddress,
			String communityResidentPhones, String communityResidentSubcontractor, Timestamp communityResidentEditTime,
			Integer communityId, Integer[] communityIds, Community community) {
		this.communityResidentId = communityResidentId;
		this.communityResidentName = communityResidentName;
		this.communityResidentAddress = communityResidentAddress;
		this.communityResidentPhones = communityResidentPhones;
		this.communityResidentSubcontractor = communityResidentSubcontractor;
		this.communityResidentEditTime = communityResidentEditTime;
		this.communityId = communityId;
		this.communityIds = communityIds;
		this.community = community;
	}
	public Integer getCommunityResidentId() {
		return communityResidentId;
	}
	public void setCommunityResidentId(Integer communityResidentId) {
		this.communityResidentId = communityResidentId;
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

	@Override
	public String toString() {
		return "CommunityResident{" +
				"communityResidentId=" + communityResidentId +
				", communityResidentName='" + communityResidentName + '\'' +
				", communityResidentAddress='" + communityResidentAddress + '\'' +
				", communityResidentPhones='" + communityResidentPhones + '\'' +
				", communityResidentSubcontractor='" + communityResidentSubcontractor + '\'' +
				", communityResidentEditTime=" + communityResidentEditTime +
				", communityId=" + communityId +
				", communityIds=" + Arrays.toString(communityIds) +
				", community=" + community +
				'}';
	}
}