package com.yfshop.shop.service.mall.req;

import lombok.Data;
import org.apache.commons.collections4.map.HashedMap;

import java.io.Serializable;
import java.util.*;

/**
 * @author Xulg
 * Created in 2021-03-29 10:42
 */
@Data
public class QueryItemDetailReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer itemId;
    private boolean querySku = true;

    public static void main(String[] args) {
        String a = "张三";
        String b = "张三";
        String c = new String("张三");
        String d = new String("张三");
        StringBuilder e = new StringBuilder("张三");
        StringBuilder f = new StringBuilder("张三");
        System.out.println(a == b);
        System.out.println(a == c);
        System.out.println(a == e.toString());
        System.out.println(c == d);
        System.out.println(c == f.toString());
        System.out.println(e.toString() == f.toString());

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("key1", "value1");
        hashMap.put("key2", "value3");
        hashMap.put("key3", "value3");
        Set<String> setList = hashMap.keySet();
        Collection<String> valueList = hashMap.values();
        for (String value : valueList) {
            System.out.println(value);
        }
        for (String key : setList) {
            System.out.println(key);
        }
        System.out.println(hashMap.containsValue("key3"));
        System.out.println(hashMap.containsValue("value3"));
        System.out.println(hashMap.isEmpty());
        System.out.println(new HashedMap<>().isEmpty());
    }

}
