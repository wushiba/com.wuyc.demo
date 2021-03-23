package com.yfshop.common.enums;

/**
 * 角色标识枚举
 * @author Xulg
 * Created in 2021-03-23 9:27
 */
public enum GroupRoleEnum {

    SYS("sys", "系统管理员"),

    ZB("zb", "总部"),

    FGS("fgs", "分公司"),

    JXS("jxs", "经销商"),

    YWY("ywy", "业务员"),

    CXY("zb", "促销员");

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    GroupRoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static GroupRoleEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (GroupRoleEnum value : values()) {
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
