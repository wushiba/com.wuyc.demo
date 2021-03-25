package com.yfshop.common.plug;

public enum PlugLocation {
    NorthWest("NorthWest", "左上"),
    North("North", "中上"),
    NorthEast("NorthEast", "右上"),
    West("West", "左中"),
    Center("Center", "中"),
    East("East", "右中"),
    SouthWest("SouthWest", "左下"),
    South("South", "中下"),
    SouthEast("SouthEast", "右下");

    private String code;
    private String name;

    private PlugLocation(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static PlugLocation getByCode(String code) {
        for (PlugLocation item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return NorthWest;
    }
}
