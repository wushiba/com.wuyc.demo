package com.wuyc.enums;

import java.util.Arrays;

/**
 * @author sp0313
 * @date 2022年11月28日 10:09:00
 */
public enum SexEnum {

    MALE(1, "男"),

    FE_MALE(2, "女"),

    UNKNOWN(3, "未知");

    /**
     * 枚举编码
     */
    private final Integer code;

    /**
     * 枚举描述
     */
    private final String description;

    SexEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SexEnum getEnumByCode(Integer code) {
        return Arrays.stream(SexEnum.values())
                .filter(data -> data.getCode().equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
