package com.github.phonenumbermanager.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 列表框视图对象
 *
 * @author 廿二月的天
 */
@ApiModel("列表框视图对象")
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class SelectListVo implements Serializable {
    private Long id;
    private String name;
    private String title;
    private String label;
    private Long value;
    private Boolean isLeaf;
    private Boolean loading;
    private List<SelectListVo> children;
}
