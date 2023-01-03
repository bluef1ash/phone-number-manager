package com.github.phonenumbermanager.vo;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "单位视图对象")
public class CompanyVO implements GrantedAuthority {
    @Schema(title = "单位编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(title = "单位名称")
    private String name;
    @Schema(title = "上级单位编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    @Schema(title = "下级单位")
    private List<CompanyVO> children;
    @Schema(title = "是否为叶子节点")
    private Boolean isLeaf;

    @Override
    public String getAuthority() {
        return String.valueOf(id);
    }
}
