package com.yfshop.common.enums;

/**
 * 牛奶盒子枚举
 * @author wuyc
 * created 2021/3/24 16:47
 **/
public enum BoxSpecValEnum {

    SMALL("small", "小盒"),

    BIG("big", "大盒");

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    BoxSpecValEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BoxSpecValEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (BoxSpecValEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
