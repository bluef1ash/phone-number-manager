package com.github.phonenumbermanager.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区居民对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "社区居民对象实体")
public class CommunityResident extends BaseEntity<CommunityResident> {
    @Schema(title = "社区居民编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "社区居民姓名")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民姓名不能为空！")
    @Length(max = 10, message = "社区居民姓名不能超过10个字符！")
    private String name;
    @Schema(title = "社区居民地址")
    @NotBlank(groups = CreateInputGroup.class, message = "社区居民地址不能为空！")
    @Length(max = 255, message = "社区居民地址不能超过255个字符！")
    private String address;
    @NotNull(groups = CreateInputGroup.class, message = "所属社区居民分包人不能为空！")
    @Schema(title = "所属社区分包人编号")
    @Min(value = 1, message = "所属社区分包人编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subcontractorId;
    @NotNull(groups = CreateInputGroup.class, message = "所属社区不能为空！")
    @Schema(title = "所属社区编号")
    @Min(value = 1, message = "所属社区编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @TableField(exist = false)
    @Schema(title = "社区居民分包人信息")
    private List<String> subcontractorInfo;
    @Schema(title = "社区居民联系方式")
    @NotNull(message = "社区居民的联系方式不能为空！")
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @Schema(hidden = true)
    private Subcontractor subcontractor;
    @TableField(exist = false)
    @Schema(hidden = true)
    private Company company;
}
