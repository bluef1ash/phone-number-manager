package com.github.phonenumbermanager.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.EducationStatusEnum;
import com.github.phonenumbermanager.constant.enums.EmploymentStatusEnum;
import com.github.phonenumbermanager.constant.enums.GenderEnum;
import com.github.phonenumbermanager.constant.enums.PoliticalStatusEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;
import com.github.phonenumbermanager.validator.PhoneNumberValidator;

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
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId(type = IdType.INPUT)
    private String id;
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长名称不能为空！")
    @Length(max = 10, message = "社区楼长姓名不允许超过10个字符！")
    private String name;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长性别不能为空！")
    private GenderEnum gender;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长生日不能为空！")
    @Past
    private Date birth;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长政治状况不能为空！")
    private PoliticalStatusEnum politicalStatus;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长就业情况不能为空！")
    private EmploymentStatusEnum employmentStatus;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长教育情况不能为空！")
    private EducationStatusEnum education;
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长地址不能为空！")
    @Length(max = 255, message = "社区楼长地址不允许超过255个字符！")
    private String address;
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长管理地址不能为空！")
    @Length(max = 255, message = "社区楼长管理地址不允许超过255个字符！")
    private String managerAddress;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长管理数不能为空！")
    @Min(value = 1, message = "社区居民楼长管理数最低为1！")
    private Integer managerCount;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长分包人不能为空！")
    @Min(value = 1, message = "所属分包人编号不正确！")
    private Long subcontractorId;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长社区不能为空！")
    @Min(value = 0, message = "所属社区编号不正确！")
    private Long communityId;
    @PhoneNumberValidator
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Company company;
}
