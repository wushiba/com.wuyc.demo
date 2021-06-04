package com.yfshop.admin.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yfshop.common.util.JuHeExpressDeliveryUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BaiduMapGeocoderUtil {

    /**
     * 百度地图 Api调用相关的百度AK
     */
    private final static String BAIDU_MAP_AK = "gzwaiTL0UHG0rwYtDUo5CaD7HxnSm1lf";

    private static void main(String[] args) {
        // getAddressInfoByLngAndLat("120.2027200000", "30.1834890000");



    }

    /**
     * 根据经纬度调用百度API获取 地理位置信息，根据经纬度
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return
     */
    public static Map<String, String> getAddressInfoByLngAndLat(String longitude, String latitude) {
        Map<String, String> map = new HashMap<>();
        String location = latitude + "," + longitude;
        String url = "http://api.map.baidu.com/reverse_geocoding/v3/?ak=" + BAIDU_MAP_AK + "&output=json&coordtype=wgs84ll&location=" + location;
        try {
            String json = HttpUtil.downloadString(url, "UTF-8");
            JSONObject obj = JSONObject.parseObject(json);
            String success = "0";
            String status = String.valueOf(obj.get("status"));
            if (success.equals(status)) {
                String result = String.valueOf(obj.get("result"));
                JSONObject resultObj = JSONObject.parseObject(result);
                JSONObject addressComponent = (JSONObject) resultObj.get("addressComponent");
                map.put("province", addressComponent.getString("province"));
                map.put("city", addressComponent.getString("city"));
                map.put("district", addressComponent.getString("district"));
                //System.out.println(map);
                return map;
            }
        } catch (Exception e) {

        }
        return null;
    }

}