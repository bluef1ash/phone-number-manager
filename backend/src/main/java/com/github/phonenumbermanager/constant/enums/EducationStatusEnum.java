package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 学历枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum EducationStatusEnum {
    /**
     *
     */
    ILLITERACY(0, "文盲"), PRIMARY_SCHOOL(1, "小学"), JUNIOR_HIGH_SCHOOL(2, "初级中学"), TECHNICAL_SECONDARY_SCHOOL(3, "中学专科"),
    SENIOR_MIDDLE_SCHOOL(4, "高级中学"), JUNIOR_COLLEGE(5, "大学专科"), UNDERGRADUATE_COURSE(6, "大学本科"), MASTER(7, "硕士研究生"),
    DOCTOR(8, "博士研究生");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static EducationStatusEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static EducationStatusEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
