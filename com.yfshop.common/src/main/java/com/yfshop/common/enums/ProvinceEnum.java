package com.yfshop.common.enums;

/**
 * @author wuyc
 * Created in 2021-03-24 19:45
 */
public enum ProvinceEnum {

    BEIJING(1, "北京", "北京"),
    TIANJING(2, "天津", "天津"),
    HEBEI(3, "河北", "河北"),
    山西(4, "山西", "山西"),
    NEIMENG(5, "内蒙", "内蒙古"),
    LIAONING(6, "辽宁", "辽宁"),
    JILIN(7, "吉林", "吉林"),
    HEILONGJIANG(8, "黑龙", "黑龙江"),
    SHANGHAI(9, "上海", "上海"),
    JIANGSU(10, "江苏", "江苏"),
    ZHEJIANG(11, "浙江", "浙江"),
    ANHUI(12, "安徽", "安徽"),
    FUJIAN(13, "福建", "福建"),
    JIANGXI(14, "江西", "江西"),
    SHANDONG(15, "山东", "山东"),
    HENAN(16, "河南", "河南"),
    HUBEI(17, "湖北", "湖北"),
    HUNAN(18, "湖南", "湖南"),
    GUANGDONG(19, "广东", "广东"),
    GUANGXI(20, "广西", "广西"),
    HAINAN(21, "海南", "海南"),
    CHONGQING(22, "重庆", "重庆"),
    SICHUAN(23, "四川", "四川"),
    GUIZHOU(24, "贵州", "贵州"),
    YUNNAN(25, "云南", "云南"),
    XIZANG(26, "西藏", "西藏"),
    陕西(27, "陕西", "陕西"),
    GANSU(28, "甘肃", "甘肃"),
    QINGHAI(29, "青海", "青海"),
    NINGXIA(30, "宁夏", "宁夏"),
    XINJIANG(31, "新疆", "新疆");

    /** 数据库对应省份id */
    private Integer id;

    /** 省份前两位 */
    private String prefix;

    /** 省份名称 */
    private String provinceName;

    ProvinceEnum(Integer id, String prefix, String provinceName) {
        this.id = id;
        this.prefix = prefix;
        this.provinceName = provinceName;
    }

    public static ProvinceEnum getByPrefix(String prefix) {
        if (prefix == null) {
            return null;
        }
        for (ProvinceEnum value : values()) {
            if (value.getPrefix().equals(prefix)) {
                return value;
            }
        }
        return null;
    }

    public static ProvinceEnum getByProvinceName(String provinceName) {
        if (provinceName == null) {
            return null;
        }
        for (ProvinceEnum value : values()) {
            if (value.getPrefix().equals(provinceName)) {
                return value;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getProvinceName() {
        return prefix;
    }
}
