package com.github.phonenumbermanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 楼片长实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class DormitoryManager implements Serializable {
    public static final String[] SEXES = {"男", "女", "未知"};
    public static final String[] POLITICAL_STATUSES = {"群众", "共产党员", "预备共产党员", "共青团员", "预备共青团员", "其它"};
    public static final String[] WORK_STATUSES = {"在职", "退休", "无业"};
    public static final String[] EDUCATIONS = {"文盲", "小学", "初中", "中专", "高中", "大专", "本科", "硕士研究生", "博士研究生"};
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
    private String phones;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long subcontractorId;
    private Long communityId;
    private String mobile;
    private String landline;
    private Subcontractor subcontractor;
    private String subcontractorName;
    private String subcontractorTelephone;
    private Community community;
    private String communityName;
}
