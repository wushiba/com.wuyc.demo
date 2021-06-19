package com.yfshop.shop.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.lionsoul.ip2region.Util;

public class BaiduIp2RegionUtil {
    private final static String BAIDU_MAP_AK = "gzwaiTL0UHG0rwYtDUo5CaD7HxnSm1lf";

    public static String getRegionByIp(String ipStr) {
        if (!Util.isIpAddress(ipStr)) {
            return null;
        }
        String url = "http://api.map.baidu.com/location/ip?ak=" + BAIDU_MAP_AK + "&ip=" + ipStr + "&coor=bd09ll";
        try {
            String json = HttpUtil.downloadString(url, "UTF-8");
            JSONObject obj = JSONObject.parseObject(json);
            String region = obj.getString("address");
            if (StringUtils.isNotBlank(region)) {
                String[] temp = region.split("\\|");
                if (temp.length==7){
                    return String.format("%s|0|%s|%s|%s",temp[0],temp[1],temp[2],temp[4]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getRegionByIp("39.144.190.106"));
    }
}
