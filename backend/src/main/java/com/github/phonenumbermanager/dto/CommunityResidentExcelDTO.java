package com.github.phonenumbermanager.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@NoArgsConstructor
@ApiModel("社区居民导出 Excel 表格数据模型")
public class CommunityResidentExcelDTO implements Serializable {
    @ApiModelProperty("街道")
    @ExcelProperty("街道")
    private String streetName;
    @ApiModelProperty("社区")
    @ExcelProperty("社区")
    private String communityName;
    @ApiModelProperty("户主姓名")
    @ExcelProperty("户主姓名")
    private String name;
    @ApiModelProperty("家庭地址")
    @ExcelProperty("家庭地址")
    private String address;
    @ApiModelProperty("电话1")
    @ExcelProperty("电话1")
    private String phone1;
    @ApiModelProperty("电话2")
    @ExcelProperty("电话2")
    private String phone2 = "";
    @ApiModelProperty("电话3")
    @ExcelProperty("电话3")
    private String phone3 = "";
    @ApiModelProperty("分包人")
    @ExcelProperty("分包人")
    private String subcontractor;
}
