package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.phonenumbermanager.constant.enums.HttpMethodEnum;

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
    private Long roleId;
    private Long privilegeId;
    private HttpMethodEnum method;
}
