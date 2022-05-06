package com.github.phonenumbermanager.vo;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单位视图对象
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("单位视图对象")
public class CompanyVo implements GrantedAuthority {
    @ApiModelProperty("单位编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty("单位名称")
    private String name;
    @ApiModelProperty("单位分包数")
    private Integer actualNumber;
    @ApiModelProperty("单位层级编号")
    private Integer level;
    @ApiModelProperty("上级单位编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @ApiModelProperty("下级单位")
    private List<CompanyVo> children;

    @Override
    public String getAuthority() {
        return String.valueOf(id);
    }
}
