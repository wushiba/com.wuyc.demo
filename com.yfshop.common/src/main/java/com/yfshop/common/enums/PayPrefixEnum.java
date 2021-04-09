package com.yfshop.common.enums;

/**
 * 支付前缀枚举
 * @author wuyc
 * Created in 2021-04-9 11:41
 */
public enum PayPrefixEnum {

    WEBSITE_CODE("websiteCode_", "网点码支付前缀"),

    USER_ORDER("userOrder_", "用户订单支付前缀");

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    PayPrefixEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PayPrefixEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (PayPrefixEnum value : values()) {
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
