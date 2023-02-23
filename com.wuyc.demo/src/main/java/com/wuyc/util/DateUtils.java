package com.wuyc.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 *
 * @author sp0313
 * @date 2022年11月29日 16:18:00
 */
public class DateUtils {

    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前是哪一年
     *
     * @return 当前年
     */
    public static int getLocalYear() {
        return LocalDateTime.now().getYear();
    }

    /**
     * 获取当前是哪一月
     *
     * @return 当前月
     */
    public static int getLocalMonth() {
        return LocalDateTime.now().getMonthValue();
    }

    /**
     * 获取今天是本周的第几天
     *
     * @return 本周第几天
     */
    public static int getWeekDay() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }

    /**
     * 获取今天是本月的第几天
     *
     * @return 本月第几天
     */
    public static int getMonthDay() {
        return LocalDateTime.now().getDayOfMonth();
    }

    /**
     * 获取今天是本年的第几天
     *
     * @return 本月第几天
     */
    public static int getYearDay() {
        return LocalDateTime.now().getDayOfYear();
    }

    /**
     * 获取当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getLocalDate() {
        return localDateTimeFormat(YYYY_MM_DD);
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getLocalDateTime() {
        return localDateTimeFormat(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化当前日期
     *
     * @return 返回传入的时间格式
     */
    public static String localDateTimeFormat(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }


}
