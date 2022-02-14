package com.github.phonenumbermanager.constant.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

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
    ILLITERACY(0, "文盲"), PRIMARY_SCHOOL(1, "小学"), JUNIOR_HIGH_SCHOOL(2, "初中"), TECHNICAL_SECONDARY_SCHOOL(3, "中学专科"),
    SENIOR_MIDDLE_SCHOOL(4, "高中"), JUNIOR_COLLEGE(5, "大学专科"), UNDERGRADUATE_COURSE(6, "大学本科"), MASTER(7, "硕士研究生"),
    DOCTOR(8, "博士研究生");

    @EnumValue
    private final int value;
    private final String description;
}
