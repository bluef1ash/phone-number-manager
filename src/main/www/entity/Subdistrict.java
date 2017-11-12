package www.entity;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
/**
 * 街道办事处实体
 *
 */
public class Subdistrict implements Serializable {
	private static final long serialVersionUID = -7676924584213521372L;
	private Integer subdistrictId;
	private String subdistrictName;
	private String subdistrictTelephone;
	private List<Community> communities;
	public Subdistrict() {}
	public Subdistrict(Integer subdistrictId, String subdistrictName, String subdistrictTelephone, List<Community> communities) {
		super();
		this.subdistrictId = subdistrictId;
		this.subdistrictName = subdistrictName;
		this.subdistrictTelephone = subdistrictTelephone;
		this.communities = communities;
	}
	public Integer getSubdistrictId() {
		return subdistrictId;
	}
	public void setSubdistrictId(Integer subdistrictId) {
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
}
