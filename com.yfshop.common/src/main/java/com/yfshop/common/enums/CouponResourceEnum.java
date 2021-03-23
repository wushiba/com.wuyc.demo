package com.yfshop.common.enums;

/**
 * 优惠券来源枚举
 * @author Xulg
 * Created in 2021-03-23 9:27
 */
public enum CouponResourceEnum {

    DRAW("draw", "活动抽奖"),

    SHOP("shop", "商城领取");

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    CouponResourceEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CouponResourceEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CouponResourceEnum value : values()) {
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
