package com.github.phonenumbermanager.entity;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单位对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("单位对象实体")
public class Company extends BaseEntity<Company> implements GrantedAuthority {
    @ApiModelProperty("单位编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @TableId
    private Long id;
    @ApiModelProperty("单位名称")
    @NotBlank(groups = CreateInputGroup.class, message = "单位名称不能为空！")
    @Length(max = 20, message = "单位名称不能超过10个字符！")
    private String name;
    @ApiModelProperty("上级单位编号")
    @NotNull(groups = CreateInputGroup.class, message = "上级单位不能为空！")
    @Min(value = 0, message = "上级单位编号不正确！")
    private Long parentId;
    @ApiModelProperty("单位联系方式")
    @NotNull(message = "单位的联系方式不能为空！")
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @ApiModelProperty("下级单位")
    private List<Company> children;
    @TableField(exist = false)
    @ApiModelProperty("单位额外属性")
    private List<CompanyExtra> companyExtras;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限不能为空！")
    @ApiModelProperty("系统权限")
    @TableField(exist = false)
    private List<SystemPermission> systemPermissions;

    @Override
    public String getAuthority() {
        return String.valueOf(id);
    }
}
