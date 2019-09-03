package com.github.phonenumbermanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 配置项实体
 *
 * @author 廿二月的天
 */
public class Configuration implements Serializable {
    private static final long serialVersionUID = 632055591097222655L;
    private String key;
    private Integer type;
    private String value;
    private String description;
    private Boolean keyChanged;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Configuration() {
    }

    public Configuration(String key, Integer type, String value, String description, Boolean keyChanged, Timestamp createTime, Timestamp updateTime) {
        this.key = key;
        this.type = type;
        this.value = value;
        this.description = description;
        this.keyChanged = keyChanged;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getKeyChanged() {
        return keyChanged;
    }

    public void setKeyChanged(Boolean keyChanged) {
        this.keyChanged = keyChanged;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Configuration that = (Configuration) o;

        if (!Objects.equals(key, that.key)) {
            return false;
        }
        if (!Objects.equals(type, that.type)) {
            return false;
        }
        if (!Objects.equals(value, that.value)) {
            return false;
        }
        if (!Objects.equals(description, that.description)) {
            return false;
        }
        if (!Objects.equals(keyChanged, that.keyChanged)) {
            return false;
        }
        if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
        return Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (keyChanged != null ? keyChanged.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" + "key='" + key + '\'' + ", type=" + type + ", value='" + value + '\'' + ", description='" + description + '\'' + ", keyChanged=" + keyChanged + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
