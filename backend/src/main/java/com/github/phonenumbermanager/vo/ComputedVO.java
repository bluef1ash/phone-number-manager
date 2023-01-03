package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 计算视图对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@Schema(title = "计算视图对象")
public class ComputedVO implements Serializable {
    @Schema(title = "需要获取的单位编号集合")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] companyIds;
    @Schema(title = "柱状图表类型参数")
    private Boolean barChartTypeParam;
}
