package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import com.github.phonenumbermanager.constant.enums.GenderEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区楼片长搜索视图对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("社区楼片长搜索视图对象")
public class DormitoryManagerSearchVo implements Serializable {
    @ApiModelProperty("单位编号")
    private Long companyId;
    @ApiModelProperty("社区楼长姓名")
    private String name;
    @ApiModelProperty("社区楼长性别")
    private GenderEnum gender;
    @ApiModelProperty("社区楼长家庭地址")
    private String address;
    @ApiModelProperty("社区楼长联系方式")
    private String phoneNumber;
}
