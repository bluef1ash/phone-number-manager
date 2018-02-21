package www.entity;

import java.io.Serializable;

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
    private Integer keyIsChanged;

    public Configuration() {
    }

    public Configuration(String key, Integer type, String value, String description, Integer keyIsChanged) {
        this.key = key;
        this.type = type;
        this.value = value;
        this.description = description;
        this.keyIsChanged = keyIsChanged;
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

    public Integer getKeyIsChanged() {
        return keyIsChanged;
    }

    public void setKeyIsChanged(Integer keyIsChanged) {
        this.keyIsChanged = keyIsChanged;
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

        return (key != null ? key.equals(that.key) : that.key == null) && (type != null ? type.equals(that.type) : that.type == null) && (value != null ? value.equals(that.value) : that.value == null) && (description != null ? description.equals(that.description) : that.description == null) && (keyIsChanged != null ? keyIsChanged.equals(that.keyIsChanged) : that.keyIsChanged == null);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (keyIsChanged != null ? keyIsChanged.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" +
            "key='" + key + '\'' +
            ", type=" + type +
            ", value='" + value + '\'' +
            ", description='" + description + '\'' +
            ", keyIsChanged=" + keyIsChanged +
            '}';
    }
}
