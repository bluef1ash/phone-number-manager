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
 * 社区实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class Community implements Serializable {
    private Long id;
    private String name;
    private String landline;
    private Integer actualNumber;
    private Boolean dormitorySubmitted;
    private Boolean residentSubmitted;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long subdistrictId;
    private List<CommunityResident> communityResidents;
    private Subdistrict subdistrict;
}
