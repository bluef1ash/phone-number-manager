package com.github.phonenumbermanager.constant;

/**
 * 计算数据的类型
 *
 * @author 廿二月的天
 */
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
     * 社区楼长基本信息
     */
    DORMITORY_BASE_MESSAGE(3),
    /**
     * 社区楼长柱状图表
     */
    DORMITORY_BAR_CHART(4);

    private int code;

    ComputedDataTypes(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }

    public int getCode() {
        return code;
    }
}
