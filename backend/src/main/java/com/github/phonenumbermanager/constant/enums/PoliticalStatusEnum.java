package com.github.phonenumbermanager.constant.enums;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 政治状况枚举
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Getter
public enum PoliticalStatusEnum {
    /**
     *
     */
    MALE(0, "群众"), PARTY_MEMBER(1, "共产党员"), PREPARATORY_COMMUNISTS(2, "预备共产党员"),
    COMMUNIST_YOUTH_LEAGUE_MEMBER(3, "共青团员"), PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER(4, "预备共青团员"), OTHER(5, "民主党派");

    @EnumValue
    @JsonValue
    private final int value;
    private final String description;

    public static PoliticalStatusEnum valueOf(int value) {
        return Arrays.stream(values()).filter(e -> value == e.getValue()).findFirst().orElse(null);
    }

    public static PoliticalStatusEnum descriptionOf(String description) {
        return Arrays.stream(values()).filter(e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }
}
