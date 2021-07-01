package com.yfshop.common.enums;

/**
 * @author Xulg
 * Created in 2021-03-23 9:27
 */
public enum BannerPositionsEnum {

    HOME("home", "首页"),

    BANNER("banner", "轮播"),

    PERSONAL_CENTER("personal_center", "个人中心"),

    CATEGORY("category", "分类"),
    ;

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    BannerPositionsEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BannerPositionsEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (BannerPositionsEnum value : values()) {
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
