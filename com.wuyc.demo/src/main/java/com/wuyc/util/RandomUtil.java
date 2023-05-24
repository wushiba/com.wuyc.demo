package com.wuyc.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author sp0313
 * @date 2023年05月04日 16:47:00
 */
public class RandomUtil {

    private static final String BASIC = "123456789qwertyuiopasdfghjklzxcvbnm";

    public static String random(int length){
        char[] basicArray = BASIC.toCharArray();
        Random random = new Random();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int index = random.nextInt(100) % (basicArray.length);
            result[i] = basicArray[index];
        }
        return new String(result);
    }

    public static void main(String[] args) {
        System.out.println(random(5));
        System.out.println(random(6));

        Set<String> dataList = new HashSet<>();
        for (int i = 0; i < 500000; i++) {
            dataList.add(RandomUtil.random(5));
        }
        System.out.println(dataList.size());
    }
}
