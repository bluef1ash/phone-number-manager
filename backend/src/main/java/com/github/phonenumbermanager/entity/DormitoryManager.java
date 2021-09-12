package com.github.phonenumbermanager.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.EducationStatusEnum;
import com.github.phonenumbermanager.constant.EmploymentStatusEnum;
import com.github.phonenumbermanager.constant.GenderEnum;
import com.github.phonenumbermanager.constant.PoliticalStatusEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 楼片长对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("楼片长对象实体")
public class DormitoryManager extends BaseEntity<DormitoryManager> {
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
    private Long subcontractorId;
    private Long communityId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Community community;
}
