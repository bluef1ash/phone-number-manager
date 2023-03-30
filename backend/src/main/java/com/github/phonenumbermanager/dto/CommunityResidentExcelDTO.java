package com.github.phonenumbermanager.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 社区居民导出 Excel 表格数据模型
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "社区居民导出 Excel 表格数据模型")
public class CommunityResidentExcelDTO implements Serializable {
    @Schema(title = "街道")
    @ExcelProperty("街道")
    private String streetName;
    @Schema(title = "社区")
    @ExcelProperty("社区")
    private String communityName;
    @Schema(title = "户主姓名")
    @ExcelProperty("户主姓名")
    private String name;
    @Schema(title = "家庭地址")
    @ExcelProperty("家庭地址")
    private String address;
    @Schema(title = "电话1")
    @ExcelProperty("电话1")
    private String phone1;
    @Schema(title = "电话2")
    @ExcelProperty("电话2")
    private String phone2 = "";
    @Schema(title = "电话3")
    @ExcelProperty("电话3")
    private String phone3 = "";
    @Schema(title = "分包人")
    @ExcelProperty("分包人")
    private String subcontractor;
}
