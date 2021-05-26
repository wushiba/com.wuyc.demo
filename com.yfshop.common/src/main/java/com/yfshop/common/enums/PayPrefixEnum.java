package com.yfshop.common.enums;

/**
 * 支付前缀枚举
 * @author wuyc
 * Created in 2021-04-9 11:41
 */
public enum PayPrefixEnum {

    WEBSITE_CODE("websiteCode-", "websiteCode", "网点码支付前缀"),

    USER_ORDER("userOrder-", "userOrder", "用户订单支付前缀"),

    HEALTHY_ORDER("healthyOrder-", "healthyOrder", "健康订单支付前缀");

    private final String prefix;

    private final String bizType;

    private final String description;

    PayPrefixEnum(String prefix, String bizType, String description) {
        this.prefix = prefix;
        this.bizType = bizType;
        this.description = description;
    }

    public static PayPrefixEnum getByCode(String prefix) {
        if (prefix == null) {
            return null;
        }
        for (PayPrefixEnum value : values()) {
            if (value.getPrefix().equals(prefix)) {
                return value;
            }
        }
        return null;
    }

    public static PayPrefixEnum getByBizType(String bizType) {
        if (bizType == null) {
            return null;
        }
        for (PayPrefixEnum value : values()) {
            if (value.getBizType().equals(bizType)) {
                return value;
            }
        }
        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getBizType() {
        return bizType;
    }

    public String getDescription() {
        return description;
    }
}
