package com.yfshop.common.enums;

/**
 * 优惠券使用状态枚举
 * @author wuyc
 * created 2021/4/1 10:03
 **/
public enum UserCouponStatusEnum {

    NO_USE("NO_USE", "未使用"),

    IN_USE("IN_USE", "使用中"),

    HAS_USE("HAS_USE", "已使用");

    private String code;

    private String description;

    UserCouponStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserCouponStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserCouponStatusEnum value : values()) {
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
