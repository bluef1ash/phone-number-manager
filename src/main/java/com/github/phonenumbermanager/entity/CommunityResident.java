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
 * 社区居民实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class CommunityResident implements Serializable {
    private Long id;
    private Integer indexId;
    private String subdistrictName;
    private String communityName;
    private String name;
    private String address;
    private String phones;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long subcontractorId;
    private Long communityId;
    private Subcontractor subcontractor;
    private String subcontractorName;
    private List<Long> communityIds;
    private Community community;
    private String phone1;
    private String phone2;
    private String phone3;
}
