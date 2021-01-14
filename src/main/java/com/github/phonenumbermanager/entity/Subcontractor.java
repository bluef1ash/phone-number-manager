package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 社区分包人实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class Subcontractor implements Serializable {
    private Long id;
    private String name;
    private String mobile;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long communityId;
    private Community community;
}
