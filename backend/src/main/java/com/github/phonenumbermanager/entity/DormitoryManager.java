package com.github.phonenumbermanager.entity;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.constant.enums.EducationStatusEnum;
import com.github.phonenumbermanager.constant.enums.EmploymentStatusEnum;
import com.github.phonenumbermanager.constant.enums.GenderEnum;
import com.github.phonenumbermanager.constant.enums.PoliticalStatusEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

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
    @ApiModelProperty("楼片长编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @ApiModelProperty("社区居民楼长名称")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长名称不能为空！")
    @Length(max = 10, message = "社区楼长姓名不允许超过10个字符！")
    private String name;
    @ApiModelProperty("社区居民楼长性别")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长性别不能为空！")
    private GenderEnum gender;
    @ApiModelProperty("社区居民楼长生日")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长生日不能为空！")
    @Past
    private LocalDate birth;
    @ApiModelProperty("社区居民楼长政治状况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长政治状况不能为空！")
    private PoliticalStatusEnum politicalStatus;
    @ApiModelProperty("社区居民楼长就业情况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长就业情况不能为空！")
    private EmploymentStatusEnum employmentStatus;
    @ApiModelProperty("社区居民楼长教育情况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长教育情况不能为空！")
    private EducationStatusEnum education;
    @ApiModelProperty("社区居民楼长地址")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长地址不能为空！")
    @Length(max = 255, message = "社区楼长地址不允许超过255个字符！")
    private String address;
    @ApiModelProperty("社区居民楼长管理地址")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼长管理地址不能为空！")
    @Length(max = 255, message = "社区楼长管理地址不允许超过255个字符！")
    private String managerAddress;
    @ApiModelProperty("社区居民楼长管理数")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长管理数不能为空！")
    @Min(value = 1, message = "社区居民楼长管理数最低为1！")
    private Integer managerCount;
    @ApiModelProperty("所属分包人编号")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长分包人不能为空！")
    @Min(value = 1, message = "所属分包人编号不正确！")
    private Long systemUserId;
    @ApiModelProperty("所属社区编号")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼长社区不能为空！")
    @Min(value = 0, message = "所属社区编号不正确！")
    private Long companyId;
    @TableField(exist = false)
    @ApiModelProperty("社区居民楼长联系方式")
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private SystemUser systemUser;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Company company;
}
