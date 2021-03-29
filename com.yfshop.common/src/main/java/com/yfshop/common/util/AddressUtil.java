package com.yfshop.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressUtil {

    public static List<Map<String, String>> addressResolution(String address) {
        String regex = "(?<province>[^省]+省|.+自治区)(?<city>[^自治州]+自治州|[^市]+市|[^盟]+盟|[^地区]+地区|.+区划)(?<county>[^区]+区|[^市]+市|[^县]+县|[^旗]+旗|.+区)?(?<town>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null;
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> row = null;
        while (m.find()) {
            row = new LinkedHashMap<>();
            province = m.group("province");
            row.put("province", province == null ? "" : province.trim());
            city = m.group("city");
            row.put("city", city == null ? "" : city.trim());
            county = m.group("county");
            row.put("county", county == null ? "" : county.trim());
            town = m.group("town");
            row.put("town", town == null ? "" : town.trim());
            list.add(row);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println("地址是：" + addressResolution("浙江省温州市龙港市龙港市人民政府"));
    }

}
