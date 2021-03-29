package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("计算视图对象")
public class ComputedVo implements Serializable {
    @ApiModelProperty("需要获取的单位编号集合")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] companyIds;
    @ApiModelProperty("柱状图表类型参数")
    private Boolean barChartTypeParam;
}
