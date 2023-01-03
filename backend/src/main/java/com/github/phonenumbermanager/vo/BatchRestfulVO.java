package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import com.github.phonenumbermanager.constant.BatchRestfulMethod;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 批量操作视图对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@Schema(title = "批量操作视图对象")
public class BatchRestfulVO implements Serializable {
    @Schema(title = "批量操作方法类型")
    @NotNull(message = "批量操作方法类型不能为空！")
    private BatchRestfulMethod method;
    @Schema(title = "批量操作数据")
    @NotNull(message = "批量操作数据不能为空！")
    private JSONArray data;
    @Schema(title = "批量操作额外数据")
    private JSONObject extra;
}
