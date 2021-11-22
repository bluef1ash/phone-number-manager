package com.github.phonenumbermanager.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社区居民搜索视图对象
 *
 * @author 廿二月的天
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("社区居民搜索视图对象")
public class CommunityResidentSearchVo implements Serializable {
    @ApiModelProperty(name = "单位编号")
    private Long companyId;
    @ApiModelProperty(name = "社区居民姓名")
    private String name;
    @ApiModelProperty(name = "社区居民家庭地址")
    private String address;
    @ApiModelProperty(name = "社区居民联系方式")
    private String phoneNumber;
}
