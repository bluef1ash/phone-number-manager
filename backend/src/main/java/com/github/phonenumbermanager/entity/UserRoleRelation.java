package com.github.phonenumbermanager.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户与角色中间对象实体
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("pm_user_role")
@ApiModel("用户与角色中间对象实体")
public class UserRoleRelation extends BaseEntity<UserRoleRelation> {
    private Long userId;
    private Long roleId;
}
