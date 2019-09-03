package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


/**
 * 街道办事处实体
 *
 * @author 廿二月的天
 */
public class Subdistrict implements Serializable {
    private static final long serialVersionUID = -7676924584213521372L;
    private Long id;
    private String name;
    private String landline;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<Community> communities;

    public Subdistrict() {
    }

    public Subdistrict(Long id, String name, String landline, Timestamp createTime, Timestamp updateTime, List<Community> communities) {
        this.id = id;
        this.name = name;
        this.landline = landline;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.communities = communities;
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

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subdistrict that = (Subdistrict) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(landline, that.landline)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        if (!Objects.equals(updateTime, that.updateTime)) {
            return false;
        }
        return Objects.equals(communities, that.communities);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (landline != null ? landline.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (communities != null ? communities.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Subdistrict{" + "id=" + id + ", name='" + name + '\'' + ", landline='" + landline + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + ", communities=" + communities + '}';
    }
}
