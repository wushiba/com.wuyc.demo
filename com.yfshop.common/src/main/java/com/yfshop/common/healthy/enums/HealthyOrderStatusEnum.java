package com.yfshop.common.healthy.enums;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-26 17:03
 */
public enum HealthyOrderStatusEnum {

    PAYING("PAYING", "支付中"),
    SERVICING("SERVICING", "服务中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCEL("CANCEL", "未支付取消"),
    CLOSED("CLOSED", "已关闭"),
    ;

    private final String code;
    private final String description;

    HealthyOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static HealthyOrderStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (HealthyOrderStatusEnum value : values()) {
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
