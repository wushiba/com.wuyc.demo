package com.yfshop.common.enums;

/**
 * 角色标识枚举
 *
 * @author Xulg
 * Created in 2021-03-23 9:27
 */
public enum GroupRoleEnum {

    SYS("sys", 0, "系统管理员"),

    ZB("zb", 1, "总部"),

    FGS("fgs", 2, "分公司"),

    SQ("sq", 2, "省区"),

    JXS("jxs", 3, "经销商"),

    FXS("fxs", 4, "分销商"),

    YWY("ywy", 4, "业务员"),

    CXY("cxy", 5, "促销员"),

    WD("wd", 6, "网点"),

    GC("gc", -1, "工厂"),

    CK("ck", -1, "仓库"),

    WL("wl", -1, "物料");

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 角色级别
     */
    private final Integer level;

    GroupRoleEnum(String code, Integer level, String description) {
        this.code = code;
        this.level = level;
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

    public Integer getLevel() {
        return level;
    }
}
