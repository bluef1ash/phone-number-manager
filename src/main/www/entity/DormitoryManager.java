package www.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

/**
 * 楼片长实体
 *
 * @author 廿二月的天
 */
public class DormitoryManager implements Serializable {
    public static final String[] SEXES = {"男", "女"};
    public static final String[] POLITICAL_STATUSES = {"群众", "共产党员", "预备共产党员", "共青团员", "预备共青团员", "其它"};
    public static final String[] WORK_STATUSES = {"在职", "退休", "无业"};
    public static final String[] EDUCATIONS = {"文盲", "小学", "初中", "中专", "高中", "大专", "本科", "硕士研究生", "博士研究生"};

    private static final long serialVersionUID = -4895783381599764636L;
    private String id;
    private Long sequenceNumber;
    private String name;
    private Integer sex;
    private String sexName;
    private Date birth;
    private String birthString;
    private Integer age;
    private Integer politicalStatus;
    private String politicalStatusName;
    private Integer workStatus;
    private String workStatusName;
    private Integer education;
    private String educationName;
    private String address;
    private String managerAddress;
    private Integer managerCount;
    private String telephones;
    private Timestamp editTime;
    private Long subcontractorId;
    private Long communityId;
    private String telephone;
    private String landline;
    private Subcontractor subcontractor;
    private String subcontractorName;
    private String subcontractorTelephone;
    private Community community;
    private String communityName;

    public DormitoryManager() {
    }

    public DormitoryManager(String id, Long sequenceNumber, String name, Integer sex, String sexName, Date birth, String birthString, Integer age, Integer politicalStatus, String politicalStatusName, Integer workStatus, String workStatusName, Integer education, String educationName, String address, String managerAddress, Integer managerCount, String telephones, Timestamp editTime, Long subcontractorId, Long communityId, String telephone, String landline, Subcontractor subcontractor, String subcontractorName, String subcontractorTelephone, Community community, String communityName) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.name = name;
        this.sex = sex;
        this.sexName = sexName;
        this.birth = birth;
        this.birthString = birthString;
        this.age = age;
        this.politicalStatus = politicalStatus;
        this.politicalStatusName = politicalStatusName;
        this.workStatus = workStatus;
        this.workStatusName = workStatusName;
        this.education = education;
        this.educationName = educationName;
        this.address = address;
        this.managerAddress = managerAddress;
        this.managerCount = managerCount;
        this.telephones = telephones;
        this.editTime = editTime;
        this.subcontractorId = subcontractorId;
        this.communityId = communityId;
        this.telephone = telephone;
        this.landline = landline;
        this.subcontractor = subcontractor;
        this.subcontractorName = subcontractorName;
        this.subcontractorTelephone = subcontractorTelephone;
        this.community = community;
        this.communityName = communityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getBirthString() {
        return birthString;
    }

    public void setBirthString(String birthString) {
        this.birthString = birthString;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(Integer politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getPoliticalStatusName() {
        return politicalStatusName;
    }

    public void setPoliticalStatusName(String politicalStatusName) {
        this.politicalStatusName = politicalStatusName;
    }

    public Integer getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
    }

    public String getWorkStatusName() {
        return workStatusName;
    }

    public void setWorkStatusName(String workStatusName) {
        this.workStatusName = workStatusName;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerAddress() {
        return managerAddress;
    }

    public void setManagerAddress(String managerAddress) {
        this.managerAddress = managerAddress;
    }

    public Integer getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(Integer managerCount) {
        this.managerCount = managerCount;
    }

    public String getTelephones() {
        return telephones;
    }

    public void setTelephones(String telephones) {
        this.telephones = telephones;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
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

    public String getSubcontractorTelephone() {
        return subcontractorTelephone;
    }

    public void setSubcontractorTelephone(String subcontractorTelephone) {
        this.subcontractorTelephone = subcontractorTelephone;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DormitoryManager that = (DormitoryManager) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(sequenceNumber, that.sequenceNumber)) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(sex, that.sex)) {
            return false;
        }
        if (!Objects.equals(sexName, that.sexName)) {
            return false;
        }
        if (!Objects.equals(birth, that.birth)) {
            return false;
        }
        if (!Objects.equals(birthString, that.birthString)) {
            return false;
        }
        if (!Objects.equals(age, that.age)) {
            return false;
        }
        if (!Objects.equals(politicalStatus, that.politicalStatus)) {
            return false;
        }
        if (!Objects.equals(politicalStatusName, that.politicalStatusName)) {
            return false;
        }
        if (!Objects.equals(workStatus, that.workStatus)) {
            return false;
        }
        if (!Objects.equals(workStatusName, that.workStatusName)) {
            return false;
        }
        if (!Objects.equals(education, that.education)) {
            return false;
        }
        if (!Objects.equals(educationName, that.educationName)) {
            return false;
        }
        if (!Objects.equals(address, that.address)) {
            return false;
        }
        if (!Objects.equals(managerAddress, that.managerAddress)) {
            return false;
        }
        if (!Objects.equals(managerCount, that.managerCount)) {
            return false;
        }
        if (!Objects.equals(telephones, that.telephones)) {
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
        if (!Objects.equals(telephone, that.telephone)) {
            return false;
        }
        if (!Objects.equals(landline, that.landline)) {
            return false;
        }
        if (!Objects.equals(subcontractor, that.subcontractor)) {
            return false;
        }
        if (!Objects.equals(subcontractorName, that.subcontractorName)) {
            return false;
        }
        if (!Objects.equals(subcontractorTelephone, that.subcontractorTelephone)) {
            return false;
        }
        if (!Objects.equals(community, that.community)) {
            return false;
        }
        return Objects.equals(communityName, that.communityName);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sequenceNumber != null ? sequenceNumber.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (sexName != null ? sexName.hashCode() : 0);
        result = 31 * result + (birth != null ? birth.hashCode() : 0);
        result = 31 * result + (birthString != null ? birthString.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (politicalStatus != null ? politicalStatus.hashCode() : 0);
        result = 31 * result + (politicalStatusName != null ? politicalStatusName.hashCode() : 0);
        result = 31 * result + (workStatus != null ? workStatus.hashCode() : 0);
        result = 31 * result + (workStatusName != null ? workStatusName.hashCode() : 0);
        result = 31 * result + (education != null ? education.hashCode() : 0);
        result = 31 * result + (educationName != null ? educationName.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (managerAddress != null ? managerAddress.hashCode() : 0);
        result = 31 * result + (managerCount != null ? managerCount.hashCode() : 0);
        result = 31 * result + (telephones != null ? telephones.hashCode() : 0);
        result = 31 * result + (editTime != null ? editTime.hashCode() : 0);
        result = 31 * result + (subcontractorId != null ? subcontractorId.hashCode() : 0);
        result = 31 * result + (communityId != null ? communityId.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (landline != null ? landline.hashCode() : 0);
        result = 31 * result + (subcontractor != null ? subcontractor.hashCode() : 0);
        result = 31 * result + (subcontractorName != null ? subcontractorName.hashCode() : 0);
        result = 31 * result + (subcontractorTelephone != null ? subcontractorTelephone.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        result = 31 * result + (communityName != null ? communityName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DormitoryManager{" + "id='" + id + '\'' + ", sequenceNumber=" + sequenceNumber + ", name='" + name + '\'' + ", sex=" + sex + ", sexName='" + sexName + '\'' + ", birth=" + birth + ", birthString='" + birthString + '\'' + ", age=" + age + ", politicalStatus=" + politicalStatus + ", politicalStatusName='" + politicalStatusName + '\'' + ", workStatus=" + workStatus + ", workStatusName='" + workStatusName + '\'' + ", education=" + education + ", educationName='" + educationName + '\'' + ", address='" + address + '\'' + ", managerAddress='" + managerAddress + '\'' + ", managerCount=" + managerCount + ", telephones='" + telephones + '\'' + ", editTime=" + editTime + ", subcontractorId=" + subcontractorId + ", communityId=" + communityId + ", telephone='" + telephone + '\'' + ", landline='" + landline + '\'' + ", subcontractor=" + subcontractor + ", subcontractorName='" + subcontractorName + '\'' + ", subcontractorTelephone='" + subcontractorTelephone + '\'' + ", community=" + community + ", communityName='" + communityName + '\'' + '}';
    }
}
