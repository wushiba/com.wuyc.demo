package com.yfshop.common.util;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressUtil {

//    public static List<Map<String, String>> addressResolution(String address) {
//        String regex = "(?<province>[^省]+省|[^市]+市|.+自治区)(?<city>[^自治州]+自治州|[^市]+市|[^盟]+盟|[^地区]+地区|.+区划)(?<county>[^市]+市|[^区]+区|[^县]+县|[^旗]+旗|.+区)?(?<town>.*)";
//        Matcher m = Pattern.compile(regex).matcher(address);
//        String province = null, city = null, county = null, town = null;
//        List<Map<String, String>> list = new ArrayList<>();
//        Map<String, String> row = null;
//        while (m.find()) {
//            row = new LinkedHashMap<>();
//            province = m.group("province");
//            row.put("province", province == null ? "" : province.trim());
//            city = m.group("city");
//            row.put("city", city == null ? "" : city.trim());
//            county = m.group("county");
//            row.put("county", county == null ? "" : county.trim());
//            town = m.group("town");
//            row.put("town", town == null ? "" : town.trim());
//            list.add(row);
//        }
//        return list;
//    }


    public static Map<String, String> addressResolution(String address) {
        Map<String, String> map = new LinkedHashMap<>();
        if (!StringUtils.hasText(address)) return map;
        address = address.replaceAll(" ", "");
        int level = 0;
        String key = "";
        for (int i = 0; i < address.length(); i++) {
            key += address.charAt(i);
            switch (level) {
                case 0:
                    if (key.contains("省") || key.contains("自治区")) {
                        map.put("province", key);
                        map.put("city", key);
                        key = "";
                        level++;
                    } else if (key.contains("市") || key.contains("特别行政区")) {
                        map.put("province", key);
                        map.put("city", "市辖区");
                        key = "";
                        level += 2;
                    }
                    break;
                case 1:
                    if (key.contains("市") || key.contains("自治州") || key.contains("盟") || key.contains("地区") || key.contains("区划")) {
                        map.put("city", key);
                        key = "";
                        level++;
                    }
                    break;
                case 2:
                    if (key.contains("市") || key.contains("区") || key.contains("县") || key.contains("旗")) {
                        map.put("county", key);
                        key = "";
                        level++;
                    }
                    break;
            }
        }
        map.put("town", key);
        return map;
    }

    public static void main(String[] args) {
        System.out.println("地址是：" + addressResolution("江西省抚州市东乡区农博城"));
    }

}
