package main.entity;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
/**
 * 社区实体
 *
 */
public class Community implements Serializable {
	private static final long serialVersionUID = 632055591097222014L;
	private Integer communityId;
	@NotNull(message="{community.communityName.isNull}")
	private String communityName;
	@NotNull(message="{community.communityTelephone.isNull}")
	private String communityTelephone;
	@NotNull(message="{community.actualNumber.isNull}")
	private Integer actualNumber;
	private Integer subdistrictId;
	private List<CommunityResident> communityResidents;
	private Subdistrict subdistrict;
	public Community() {}
	public Community(Integer communityId, String communityName, String communityTelephone, Integer actualNumber, Integer subdistrictId,
			List<CommunityResident> communityResidents, Subdistrict subdistrict) {
		this.communityId = communityId;
		this.communityName = communityName;
		this.communityTelephone = communityTelephone;
		this.actualNumber = actualNumber;
		this.subdistrictId = subdistrictId;
		this.communityResidents = communityResidents;
		this.subdistrict = subdistrict;
	}
	public Integer getCommunityId() {
		return communityId;
	}
	public void setCommunityId(Integer communityId) {
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
	public Integer getSubdistrictId() {
		return subdistrictId;
	}
	public Integer getActualNumber() {
		return actualNumber;
	}
	public void setActualNumber(Integer actualNumber) {
		this.actualNumber = actualNumber;
	}
	public void setSubdistrictId(Integer subdistrictId) {
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
	public String toString() {
		return "Community [communityId=" + communityId + ", communityName=" + communityName + ", communityTelephone="
				+ communityTelephone + ", actualNumber=" + actualNumber + ", subdistrictId=" + subdistrictId
				+ ", communityResidents=" + communityResidents + ", subdistrict=" + subdistrict + "]";
	}
}