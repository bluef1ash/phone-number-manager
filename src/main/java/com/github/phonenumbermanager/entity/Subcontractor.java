package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 社区分包人实体
 *
 * @author 廿二月的天
 */
public class Subcontractor implements Serializable {
    private static final long serialVersionUID = -5689525073729148400L;
    private Long id;
    private String name;
    private String mobile;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long communityId;
    private Community community;

    public Subcontractor() {
    }

    public Subcontractor(Long id, String name, String mobile, Timestamp createTime, Timestamp updateTime, Long communityId, Community community) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.communityId = communityId;
        this.community = community;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subcontractor that = (Subcontractor) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(mobile, that.mobile)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, that.updateTime)) {
            return false;
        }
        if (!Objects.equals(communityId, that.communityId)) {
            return false;
        }
        return Objects.equals(community, that.community);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (communityId != null ? communityId.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Subcontractor{" + "id=" + id + ", name='" + name + '\'' + ", mobile='" + mobile + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + ", communityId=" + communityId + ", community=" + community + '}';
    }
}
