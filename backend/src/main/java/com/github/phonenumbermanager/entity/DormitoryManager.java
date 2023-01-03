package com.github.phonenumbermanager.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.phonenumbermanager.constant.enums.EducationStatusEnum;
import com.github.phonenumbermanager.constant.enums.EmploymentStatusEnum;
import com.github.phonenumbermanager.constant.enums.GenderEnum;
import com.github.phonenumbermanager.constant.enums.PoliticalStatusEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区居民楼片长对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "社区居民楼片长对象实体")
public class DormitoryManager extends BaseEntity<DormitoryManager> {
    @Schema(title = "社区居民楼片长编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "社区居民楼片长名称")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼片长名称不能为空！")
    @Length(max = 10, message = "社区楼片长姓名不允许超过10个字符！")
    private String name;
    @Schema(title = "社区居民楼片长性别")
    private GenderEnum gender;
    @Schema(title = "社区居民楼片长身份证号码")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长身份证号码不能为空！")
    @Pattern(
        regexp = "^([1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|([1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3})$",
        message = "社区居民楼片长身份证号码不正确！")
    private String idNumber;
    @Schema(title = "社区居民楼片长出生年月")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDate birth;
    @Schema(title = "社区居民楼片长年龄", hidden = true)
    @TableField(exist = false)
    private Integer age;
    @Schema(title = "社区居民楼片长政治状况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长政治状况不能为空！")
    private PoliticalStatusEnum politicalStatus;
    @Schema(title = "社区居民楼片长就业情况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长就业情况不能为空！")
    private EmploymentStatusEnum employmentStatus;
    @Schema(title = "社区居民楼片长教育情况")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长教育情况不能为空！")
    private EducationStatusEnum education;
    @Schema(title = "社区居民楼片长地址")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼片长地址不能为空！")
    @Length(max = 255, message = "社区楼片长地址不允许超过255个字符！")
    private String address;
    @Schema(title = "社区居民楼片长管理地址")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民楼片长管理地址不能为空！")
    @Length(max = 255, message = "社区楼片长管理地址不允许超过255个字符！")
    private String managerAddress;
    @Schema(title = "社区居民楼片长管理数")
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长管理数不能为空！")
    @Min(value = 1, message = "社区居民楼片长管理数最低为1！")
    private Integer managerCount;
    @Schema(title = "所属分包人编号")
    @Min(value = 1, message = "所属分包人编号不正确！")
    private Long subcontractorId;
    @Schema(title = "所属社区编号")
    @Min(value = 0, message = "所属社区编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @TableField(exist = false)
    @Schema(title = "社区居民楼片长联系方式")
    private List<PhoneNumber> phoneNumbers;
    @NotNull(groups = CreateInputGroup.class, message = "社区居民楼片长分包人不能为空！")
    @TableField(exist = false)
    @Schema(title = "社区居民楼片长分包人信息")
    private List<String> subcontractorInfo;
    @TableField(exist = false)
    @Schema(hidden = true)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    @Schema(hidden = true)
    private Company company;
}
