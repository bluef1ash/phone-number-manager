package com.github.phonenumbermanager.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单位对象实体
 *
 * @author 廿二月的天
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(title = "单位对象实体")
public class Company extends BaseEntity<Company> implements GrantedAuthority {
    @Schema(title = "单位编号")
    @NotNull(groups = ModifyInputGroup.class, message = "修改时编号不能为空！")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;
    @Schema(title = "单位名称")
    @NotBlank(groups = CreateInputGroup.class, message = "单位名称不能为空！")
    @Length(max = 20, message = "单位名称不能超过10个字符！")
    private String name;
    @Schema(title = "上级单位编号")
    @NotNull(groups = CreateInputGroup.class, message = "上级单位不能为空！")
    @Min(value = 0, message = "上级单位编号不正确！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @Schema(title = "单位联系方式")
    @NotNull(message = "单位的联系方式不能为空！")
    @TableField(exist = false)
    private List<PhoneNumber> phoneNumbers;
    @TableField(exist = false)
    @Schema(title = "是否为叶子节点")
    private Boolean isLeaf;
    @TableField(exist = false)
    @Schema(title = "下级单位")
    private List<Company> children;
    @TableField(exist = false)
    @Schema(title = "单位额外属性")
    private List<CompanyExtra> companyExtras;
    @NotNull(groups = CreateInputGroup.class, message = "系统权限不能为空！")
    @Schema(title = "系统权限")
    @TableField(exist = false)
    private List<SystemPermission> systemPermissions;

    @Override
    public String getAuthority() {
        return String.valueOf(id);
    }
}
