package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 异步导入、导出状态枚举
 *
 * @author 廿二月的天
 */
@Getter
@AllArgsConstructor
@ToString
public enum ImportOrExportStatusEnum {
    /**
     *
     */
    START(0, "开始运行"), HANDLING(1, "处理中"), HANDLED(2, "处理完成"), DOWNLOAD(3, "下载"), UPLOAD(4, "上传"), DONE(5, "运行成功"),
    FAILED(6, "运行失败");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static ImportOrExportStatusEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static ImportOrExportStatusEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
