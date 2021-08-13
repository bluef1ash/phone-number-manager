package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 学历枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum EducationStatusEnum implements IEnum<Integer> {
    /**
     *
     */
    ILLITERACY(0, "文盲"), PRIMARY_SCHOOL(1, "小学"), JUNIOR_HIGH_SCHOOL(2, "初中"), TECHNICAL_SECONDARY_SCHOOL(3, "中专"),
    SENIOR_MIDDLE_SCHOOL(4, "高中"), JUNIOR_COLLEGE(5, "大专"), UNDERGRADUATE_COURSE(6, "本科"), MASTER(7, "硕士研究生"),
    DOCTOR(8, "博士研究生");

    private final int value;
    private final String description;

    EducationStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
