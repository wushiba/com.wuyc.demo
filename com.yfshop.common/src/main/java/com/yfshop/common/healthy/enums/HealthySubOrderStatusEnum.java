package com.yfshop.common.healthy.enums;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-31 10:54
 */
public enum HealthySubOrderStatusEnum {

    /**
     * 待分配给经销商
     */
    WAIT_ALLOCATE("WAIT_ALLOCATE", "待分配"),

    /**
     * 等待经销商分配
     */
    IN_CIRCULATION("IN_CIRCULATION", "流转中"),

    /**
     * 等待配送
     */
    WAIT_DELIVERY("WAIT_DELIVERY", "待配送"),

    /**
     * 配送中
     */
    IN_DELIVERY("IN_DELIVERY", "配送中"),

    /**
     * 配送完成
     */
    COMPLETE_DELIVERY("COMPLETE_DELIVERY", "配送完成"),

    /**
     * 订单已关闭
     */
    CLOSED("CLOSED", "已关闭");

    private final String code;

    private final String description;

    HealthySubOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static HealthySubOrderStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (HealthySubOrderStatusEnum value : values()) {
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
