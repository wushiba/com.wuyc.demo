package com.wuyc.util.test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author wuyc
 * @date 2023年02月03日 16:59:00
 */
public class DateUtilsTest {

    public static void main(String[] args) {
//        System.out.println(DateUtils.getLocalYear());
//        System.out.println(DateUtils.getLocalMonth());
//        System.out.println(DateUtils.getWeekDay());
//        System.out.println(DateUtils.getMonthDay());
//        System.out.println(DateUtils.getYearDay());
//
//        System.out.println(DateUtils.getLocalDate());
//        System.out.println(DateUtils.getLocalDateTime());
//        System.out.println(DateUtils.localDateTimeFormat(DateUtils.YYYY_MM_DD));

//        Date nowDate = new Date();
//        Date beforeNowDate = new Date();
//        Date afterNowDate = new Date();
//        nowDate.setTime(1765303650000L);
//        beforeNowDate.setTime(1665303650000L);
//        afterNowDate.setTime(1893477900000L);
//        beforeNowDate.setTime(1765303650000L);
//        afterNowDate.setTime(1765303650000L);
//        System.out.println(nowDate.getTime());
//        System.out.println(nowDate.after(beforeNowDate));
//        System.out.println(nowDate.before(afterNowDate));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        System.out.println(date);
        System.out.println(date.getTime());
    }


}
