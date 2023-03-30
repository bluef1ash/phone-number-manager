package com.github.phonenumbermanager.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "社区居民楼片长导出 Excel 表格数据模型")
public class DormitoryManagerExcelDTO implements Serializable {
    @Schema(title = "序号")
    @ExcelProperty(value = "序号", index = 0)
    private Integer sequenceNumber;
    @Schema(title = "街道名称")
    @ExcelProperty(value = "街道名称", index = 1)
    private String streetName;
    @Schema(title = "社区名称")
    @ExcelProperty(value = "社区名称", index = 2)
    private String communityName;
    @Schema(title = "编号")
    @ExcelProperty(value = "编号", index = 3)
    private String id;
    @Schema(title = "姓名")
    @ExcelProperty(value = "姓名", index = 4)
    private String name;
    @Schema(title = "性别")
    @ExcelProperty(value = "性别", index = 5)
    private String gender;
    @Schema(title = "身份证号码")
    @ExcelProperty(value = "身份证号码", index = 6)
    private String idNumber;
    @Schema(title = "政治面貌")
    @ExcelProperty(value = "政治面貌", index = 7)
    private String politicalStatus;
    @Schema(title = "工作状况")
    @ExcelProperty(value = "工作状况", index = 8)
    private String employmentStatus;
    @Schema(title = "文化程度")
    @ExcelProperty(value = "文化程度", index = 9)
    private String education;
    @Schema(title = "家庭住址")
    @ExcelProperty(value = "家庭住址（具体到单元号、楼号）", index = 10)
    private String address;
    @Schema(title = "分包楼栋")
    @ExcelProperty(value = "分包楼栋（具体到单元号、楼号）", index = 11)
    private String managerAddress;
    @Schema(title = "联系户数")
    @ExcelProperty(value = "联系户数", index = 12)
    private Integer managerCount;
    @Schema(title = "手机")
    @ExcelProperty(value = "手机", index = 13)
    private String mobile;
    @Schema(title = "座机")
    @ExcelProperty(value = "座机", index = 14)
    private String fixedLine;
    @Schema(title = "分包人姓名")
    @ExcelProperty(value = {"分包人", "姓名"}, index = 15)
    private String subcontractorName;
    @Schema(title = "分包人手机")
    @ExcelProperty(value = {"分包人", "手机"}, index = 16)
    private String subcontractorMobile;
}
