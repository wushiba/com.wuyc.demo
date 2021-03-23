package com.yfshop.common.enums;

/**
 * @author Xulg
 * Created in 2021-03-23 9:40
 */
public enum ReceiveWayEnum {

    ALL("ALL", "配送自提都支持"),
    ZT("ZT", "自提"),
    PS("PS", "配送");

    /**
     * 枚举编码
     */
    private String code;

    /**
     * 枚举描述
     */
    private String description;

    ReceiveWayEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReceiveWayEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ReceiveWayEnum value : values()) {
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
