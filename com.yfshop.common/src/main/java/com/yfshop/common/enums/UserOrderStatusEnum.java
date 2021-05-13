package com.yfshop.common.enums;

/**
 * 用户订单状态枚举
 *
 * @author wuyc
 * created 2021/4/1 10:03
 **/
public enum UserOrderStatusEnum {

    WAIT_PAY("WAIT_PAY", "待付款"),
    CANCEL("CANCEL", "已取消"),
    WAIT_DELIVERY("WAIT_DELIVERY", "待发货"),
    WAIT_RECEIVE("WAIT_RECEIVE", "待收货"),
    SUCCESS("SUCCESS", "已完成"),
    CLOSED("CLOSED", "已关闭");

    private String code;

    private String description;

    UserOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserOrderStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserOrderStatusEnum value : values()) {
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
