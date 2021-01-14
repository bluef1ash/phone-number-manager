package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**
 * 街道办事处实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class Subdistrict implements Serializable {
    private static final long serialVersionUID = -7676924584213521372L;
    private Long id;
    private String name;
    private String landline;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<Community> communities;
}
