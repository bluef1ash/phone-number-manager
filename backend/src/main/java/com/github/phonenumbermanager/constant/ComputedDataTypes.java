package com.github.phonenumbermanager.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 计算数据的类型
 *
 * @author 廿二月的天
 */
@Getter
@AllArgsConstructor
@ToString
public enum ComputedDataTypes {
    /**
     * 社区居民基本信息
     */
    RESIDENT_BASE_MESSAGE(1),
    /**
     * 社区居民柱状图表
     */
    RESIDENT_BAR_CHART(2),
    /**
     * 社区居民分包人柱状图表
     */
    RESIDENT_SUBCONTRACTOR_BAR_CHART(5),
    /**
     * 社区楼长基本信息
     */
    DORMITORY_BASE_MESSAGE(3),
    /**
     * 社区楼长柱状图表
     */
    DORMITORY_BAR_CHART(4);

    private final int code;
}
