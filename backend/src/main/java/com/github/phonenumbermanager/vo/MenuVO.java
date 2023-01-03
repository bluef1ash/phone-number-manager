package com.github.phonenumbermanager.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 菜单视图对象
 *
 * @author 廿二月的天
 */
@Schema(title = "菜单视图对象")
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class MenuVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String path;
    private String icon;
    private String component;
    private String access;
    private List<MenuVO> routes;
    private Boolean hideChildrenInMenu;
    private Boolean hideInMenu;
    private String locale;
    private String key;
    private String[] pro_layout_parentKeys;
    private String[] parentKeys;
}
