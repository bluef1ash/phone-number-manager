package com.github.phonenumbermanager.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.phonenumbermanager.constant.enums.HttpMethodEnum;
import com.github.phonenumbermanager.validator.CreateInputGroup;
import com.github.phonenumbermanager.validator.ModifyInputGroup;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户角色与用户权限中间对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_role_privilege")
@ApiModel("用户角色与用户权限中间对象实体")
public class RolePrivilegeRelation extends BaseEntity<RolePrivilegeRelation> {
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "用户角色编号不能为空！")
    @Min(value = 0, message = "用户角色编号不正确！")
    private Long roleId;
    @NotNull(groups = {CreateInputGroup.class, ModifyInputGroup.class}, message = "用户权限编号不能为空！")
    @Min(value = 0, message = "用户权限编号不正确！")
    private Long privilegeId;
    private HttpMethodEnum method;
}
