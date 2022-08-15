package com.github.phonenumbermanager.entity;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区分包人员对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("社区分包人员对象实体")
public class Subcontractor extends BaseEntity<Subcontractor> {
    @ApiModelProperty("社区分包人员编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @ApiModelProperty("社区分包人员姓名")
    @NotBlank(groups = CreateInputGroup.class, message = "社区分包人员姓名不能为空！")
    @Length(max = 10, message = "社区分包人员姓名不允许超过10个字符！")
    private String name;
    @ApiModelProperty("社区分包人员身份证号码")
    @NotBlank(groups = CreateInputGroup.class, message = "社区分包人员身份证号码不能为空！")
    @Pattern(
        regexp = "^([1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|([1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3})$",
        message = "社区分包人员身份证号码不正确！")
    private String idCardNumber;
    @ApiModelProperty("社区分包人员职务")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] positions;
    @ApiModelProperty("社区分包人员职称")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] titles;
    @ApiModelProperty("社区分包人员单位编号")
    @NotNull(groups = CreateInputGroup.class, message = "社区分包人员单位编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @ApiModelProperty("社区分包人员联系方式集合")
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @ApiModelProperty("社区分包人员所属单位")
    @TableField(exist = false)
    private Company company;
}
