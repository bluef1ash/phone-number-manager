package com.github.phonenumbermanager.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 政治状况枚举
 *
 * @author 廿二月的天
 */
@Getter
public enum PoliticalStatusEnum implements IEnum<Integer> {
    /**
     *
     */
    MALE(0, "群众"), PARTY_MEMBER(1, "共产党员"), PREPARATORY_COMMUNISTS(2, "预备共产党员"),
    COMMUNIST_YOUTH_LEAGUE_MEMBER(3, "共青团员"), PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER(4, "预备共青团员"), OTHER(5, "其他");

    private final int value;
    private final String description;

    PoliticalStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    @Override
    public Integer getValue() {
        return this.value;
    }
}
