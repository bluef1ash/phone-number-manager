package com.github.phonenumbermanager.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 社区居民楼片长导出 Excel 表格数据模型
 *
 * @author 廿二月的天
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@ApiModel("社区居民楼片长导出 Excel 表格数据模型")
public class DormitoryManagerExcelDTO implements Serializable {
    @ApiModelProperty("序号")
    @ExcelProperty(value = "序号", index = 0)
    private Integer sequenceNumber;
    @ApiModelProperty("街道名称")
    @ExcelProperty(value = "街道名称", index = 1)
    private String streetName;
    @ApiModelProperty("社区名称")
    @ExcelProperty(value = "社区名称", index = 2)
    private String communityName;
    @ApiModelProperty("编号")
    @ExcelProperty(value = "编号", index = 3)
    private String id;
    @ApiModelProperty("姓名")
    @ExcelProperty(value = "姓名", index = 4)
    private String name;
    @ApiModelProperty("性别")
    @ExcelProperty(value = "性别", index = 5)
    private String gender;
    @ApiModelProperty("身份证号码")
    @ExcelProperty(value = "身份证号码", index = 6)
    private String idNumber;
    @ApiModelProperty("政治面貌")
    @ExcelProperty(value = "政治面貌", index = 7)
    private String politicalStatus;
    @ApiModelProperty("工作状况")
    @ExcelProperty(value = "工作状况", index = 8)
    private String employmentStatus;
    @ApiModelProperty("文化程度")
    @ExcelProperty(value = "文化程度", index = 9)
    private String education;
    @ApiModelProperty("家庭住址")
    @ExcelProperty(value = "家庭住址（具体到单元号、楼号）", index = 10)
    private String address;
    @ApiModelProperty("分包楼栋")
    @ExcelProperty(value = "分包楼栋（具体到单元号、楼号）", index = 11)
    private String managerAddress;
    @ApiModelProperty("联系户数")
    @ExcelProperty(value = "联系户数", index = 12)
    private Integer managerCount;
    @ApiModelProperty("手机")
    @ExcelProperty(value = "手机", index = 13)
    private String mobile;
    @ApiModelProperty("座机")
    @ExcelProperty(value = "座机", index = 14)
    private String fixedLine;
    @ApiModelProperty("分包人姓名")
    @ExcelProperty(value = {"分包人", "姓名"}, index = 15)
    private String subcontractorName;
    @ApiModelProperty("分包人手机")
    @ExcelProperty(value = {"分包人", "手机"}, index = 16)
    private String subcontractorMobile;
}
