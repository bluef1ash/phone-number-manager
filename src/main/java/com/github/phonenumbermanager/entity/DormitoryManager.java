package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.phonenumbermanager.constant.EducationStatusEnum;
import com.github.phonenumbermanager.constant.EmploymentStatusEnum;
import com.github.phonenumbermanager.constant.GenderEnum;
import com.github.phonenumbermanager.constant.PoliticalStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 楼片长实体
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DormitoryManager implements Serializable {
    @TableId(type = IdType.INPUT)
    private String id;
    private String name;
    private GenderEnum gender;
    private Date birth;
    private PoliticalStatusEnum politicalStatus;
    private EmploymentStatusEnum employmentStatus;
    private EducationStatusEnum education;
    private String address;
    private String managerAddress;
    private Integer managerCount;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Version
    private Integer version;
    private Long subcontractorId;
    private Long communityId;
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    private Community community;
}
