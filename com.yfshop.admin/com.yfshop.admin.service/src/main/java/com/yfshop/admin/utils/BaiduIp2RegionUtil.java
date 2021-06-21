package com.yfshop.admin.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.model.Region;
import org.apache.commons.lang.StringUtils;
import org.lionsoul.ip2region.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<String> list = FileUtil.readUtf8Lines(new File("H://9.txt"));
        List<String> tag = new ArrayList<>();
        list.forEach(item -> {
            String[] s = item.split(",");
            String ip = BaiduIp2RegionUtil.getRegionByIp(s[1]);
            if (ip != null) {
                tag.add(String.format("update yf_draw_record r set r.user_location = '%s',r.ip_region = '%s' where r.id = %s;",
                        ip.split("\\|")[2], ip, s[0]
                ));
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        FileUtil.writeLines(tag, new File("H://10.txt"), "UTF-8");
    }
}
