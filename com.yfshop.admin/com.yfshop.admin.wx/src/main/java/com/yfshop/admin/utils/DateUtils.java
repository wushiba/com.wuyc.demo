package com.yfshop.admin.utils;

import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tookbra on 2016/3/8.
 */
public class DateUtils {
    public static final SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

    public static final SimpleDateFormat longDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final SimpleDateFormat longDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat shortDateFormat2 = new SimpleDateFormat("yyyy-MM");

    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    public final static String YYYYMMDD = "yyyyMMdd";

    public final static String YYYYMM = "yyyy-MM";

    public final static String HH_MM_SS = "HH:mm:ss";

    public final static String HH_MM = "HH:mm";

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.sss";

    public static final List<String> houstList =  Lists.newArrayList("01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","00");

    public static String format(Date date, String format) {
        return null == date ? "" : new SimpleDateFormat(format).format(date);
    }

    /**
     * 日期字符串
     * @param date 时间
     * @return
     */
    public static String format(Date date) {
        return format(date, DEFAULT_FORMAT);
    }

    public static String format2(Date date) {
        return format(date, YYYY_MM_DD);
    }

    public static String format3(Date date) {
        return format(date, YYYY_MM_DD_HH_MM);
    }

    public static String format4(Date date) {
        return format(date, HH_MM);
    }

    public static String format5(Date date) {
        return format(date, YYYYMM);
    }

    public static String format6(Date date) {
        return format(date, HH_MM_SS);
    }

    public static Date getCurrentDate() throws Exception{
        return getCurrentDate(DateUtils.DEFAULT_FORMAT);
    }

    public static Date getNow() {
        return new Date();
    }

    /**
     * Date类型转LocalDate类型
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 根据指定格式获取当前时间
     * @param format
     * @return
     */
    public static String getCurrentTime(String format){
        SimpleDateFormat sdf = getFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获取指定格式的当前时间：为空时格式为yyyy-mm-dd HH:mm:ss
     * @param format
     * @return
     * @throws Exception
     */
    public static Date getCurrentDate(String format) throws Exception{
        SimpleDateFormat sdf = getFormat(format);
        String dateS = getCurrentTime(format);
        Date date = null;
        try {
            date = sdf.parse(dateS);
        } catch (ParseException e) {
            throw new Exception("时间转换出错..");
        }
        return date;
    }


    public static Date getStartTimeOfDay(Date time) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(time);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        return todayStart.getTime();
    }


    public static Date getEndTimeOfDay(Date time) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(time);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTime();
    }

    public static Date format(String value) throws ParseException {
        return longDateFormat1.parse(value);
    }

    public static Date formatDate(Date value,String format) {
        return DateUtils.stringToDate(DateUtils.format(value,format),format);
    }
    /**
     * @ClassName: DateUtils
     * @Description:
     * @Author: chenDong
     * @Date: 2018/1/15 15:52
     * @Remark:
     */
    public static Date format2(String value) throws ParseException {
        return shortDateFormat2.parse(value);
    }

    public static Date format3(String value) throws ParseException {
        return shortDateFormat.parse(value);
    }

    public static Date format4(String value) throws ParseException {
        return longDateFormat2.parse(value);
    }
    public static Date getDateAfterSecond() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, 5);
        return now.getTime();
    }
    /**
     * 验证结束时间是否大于开始时间
     * @param beginTime
     *              开始时间
     * @param endTime
     *              结束时间
     * @return boolean 是true，否false
     */
    public static boolean compareDate(String beginTime, String endTime) throws ParseException {
        Date beginDate = longDateFormat1.parse(beginTime);
        Date endDate = longDateFormat1.parse(endTime);
        if(beginDate.getTime() > endDate.getTime()) {
            return  false;
        } else {
            return true;
        }
    }
    // 获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }
    public  static int getWeekNumber(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w;
    }

    // 获取请求周的开始时间
    public static Date getBeginDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }
    // 获取本周的结束时间
    public static Date getEndDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }


    // 获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }
    //获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
              Calendar calendar = Calendar.getInstance();
            if(null != d) calendar.setTime(d);
           calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return new Timestamp(calendar.getTimeInMillis());
       }
   //获取某个日期的结束时间
       public static Date getDayEndTime(Date d) {
           Date resp = new Date();
           Calendar calendar = Calendar.getInstance();
            if(null != d) calendar.setTime(d);
             calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 23, 30, 00);
               calendar.set(Calendar.MILLISECOND, 000);
                return calendar.getTime();
          }
    /**
     * 验证结束时间是否大于开始时间
     * @param beginTime
     *              开始时间
     * @param endTime
     *              结束时间
     * @return boolean 是true，否false
     */
    public static boolean compareDate(Date beginTime, Date endTime) {
        if(beginTime.getTime() > endTime.getTime()) {
            return  false;
        } else {
            return true;
        }
    }

    /**
     * 验证结束时间是否大于开始时间或者相等
     * @param beginTime
     *              开始时间
     * @param endTime
     *              结束时间
     * @return boolean 大0，小-1，相等1
     */
    public static int compareDate1(Date beginTime, Date endTime) {
        if(beginTime.getTime() > endTime.getTime()) {
            return  0;
        } else if(beginTime.getTime() == endTime.getTime()){
            return 1;
        }else {
            return -1;
        }
    }

    /**
     * 获取日期显示格式，为空默认为yyyy-mm-dd HH:mm:ss
     * @param format
     * @return
     */
    private static SimpleDateFormat getFormat(String format){
        if(format == null || "".equals(format)){
            format = DateUtils.DEFAULT_FORMAT;
        }
        return new SimpleDateFormat(format);
    }

    /**
     * 时间分钟加减
     * @param date 时间
     * @param minutes 分钟
     * @return
     */
    public static Date getDateAfterMinute(Date date, int minutes) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minutes);
        return now.getTime();
    }

    public static Date getDateAfterMinute() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 5);
        return now.getTime();
    }

    public static Date getDateBeforeMinute(Date date, int minutes) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - minutes);
        return now.getTime();
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date
     *            日期
     * @param dateType
     *            类型
     * @param amount
     *            数值
     * @return 计算后日期
     */
    private static Date addDate(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }


    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date
     *            日期
     * @param yearAmount
     *            增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static Date addYear(Date date, int yearAmount) {
        return addDate(date, Calendar.YEAR, yearAmount);
    }

    public static Date getBeginDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), 0, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static List<String> getMonthList(String beginTime, String endTime) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        List<String> monthList = new ArrayList<String>();
        Date begin = stringToDate(beginTime,DateUtils.YYYY_MM_DD);
        Date end = stringToDate(endTime,DateUtils.YYYY_MM_DD);
        int months = (end.getYear() - begin.getYear()) * 12
                + (end.getMonth() - begin.getMonth());

        for (int i = 0; i <= months; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(begin);
            calendar.add(Calendar.MONTH, i);
            monthList.add(monthFormat.format(calendar.getTime()));
        }
        return monthList;
    }


    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date
     *            日期
     * @param monthAmount
     *            增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static Date addMonth(Date date, int monthAmount) {
        return addDate(date, Calendar.MONTH, monthAmount);
    }


    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date
     *            日期
     * @param dayAmount
     *            增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int dayAmount) {
        return addDate(date, Calendar.DATE, dayAmount);
    }


    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date
     *            日期
     * @param hourAmount
     *            增加数量。可为负数
     * @return 增加小时后的日期
     */
    public static Date addHour(Date date, int hourAmount) {
        return addDate(date, Calendar.HOUR_OF_DAY, hourAmount);
    }


    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date
     *            日期
     * @param minuteAmount
     *            增加数量。可为负数
     * @return 增加分钟后的日期
     */
    public static Date addMinute(Date date, int minuteAmount) {
        return addDate(date, Calendar.MINUTE, minuteAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date
     *            日期
     * @param secondAmount
     *            增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    public static Date addSecond(Date date, int secondAmount) {
        return addDate(date, Calendar.SECOND, secondAmount);
    }

    /**
     * 时间差（分）
     * @param begin 开始时间
     * @param end 结束时间
     * @return Long
     */
    public static Long diffTime(Date begin, Date end) {
        return (end.getTime() - begin.getTime())/(1000 * 60) % 60;
    }

    public static Long diffTime2(Date begin, Date end) {
        return (end.getTime() - begin.getTime())/(1000 * 60);
    }

    /*
    时间差（秒）
     */
    public static Long diffTime3(Date begin, Date end) {
        return (end.getTime() - begin.getTime())/1000;
    }

    /**
     * 验证时间格式是否正确
     * @param time
     * @return
     */
    public static boolean validateDate(String time) {
        String format = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
                + "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    /**
     * 将指定的字符串解析成日期类型
     *
     * @param dateStr
     *            字符串格式的日期
     * @return
     */
    public static Date stringToDate(String dateStr, String pattern) {
        SimpleDateFormat format = getFormat(pattern);
        Date date = null;
        try {
            date =  format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static Date stringToDate(String dateStr) {
        if (dateStr.length() == 7) {
            return stringToDate(dateStr, YYYYMM);
        } else if (dateStr.length() == 10) {
            return stringToDate(dateStr, YYYY_MM_DD);
        } else if(dateStr.length() == 16) {
            return stringToDate(dateStr, YYYY_MM_DD_HH_MM);
        } else if (dateStr.length() == 19) {
            return stringToDate(dateStr, YYYY_MM_DD_HH_MM_SS);
        } else {
            try {
                return stringToDate(dateStr, YYYY_MM_DD_HH_MM_SS);
            } catch (Exception e) {
                throw new RuntimeException("不支持的日期格式.");
            }
        }
    }


    /**
     * 返回unix时间戳 (1970年至今的秒数)
     * @return
     */
    public static long getUnixStamp(){
        return System.currentTimeMillis()/1000;
    }

    public static long getTodayTimeStamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static List<String> getAllHour() {
        return houstList;
    }

    public static List<String> getSubDate(String startDate, String endDate) {
        List<String> list = Lists.newArrayList();
        if (startDate.equals(endDate)) {
            return list;
        }
        Date start = stringToDate(startDate);
        Date end = stringToDate(endDate);
        Calendar calendar = Calendar.getInstance();
        while(start.before(end) || start.equals(end)) {
            calendar.setTime(start);
            list.add(format(calendar.getTime(),YYYY_MM_DD));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start = calendar.getTime();
        }
        return list;
    }

    public static List<String> getSubWeek(String startDate, String endDate) {
        List<String> list = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        Date start = stringToDate(startDate);
        Date end = stringToDate(endDate);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        while(start.before(end)) {
            calendar.setTime(start);
            list.add(format(calendar.getTime(),YYYY_MM_DD));
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
            calendar.add(Calendar.DATE, 7);
//            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            start = calendar.getTime();
        }
        return list;
    }

    public static List<String> getSubHour() {
        return null;
    }

    public static boolean sclInWeek(String startDate, String endDate) {
        Date start = stringToDate(startDate);
        Date end = stringToDate(endDate);
        long day = (end.getTime()-start.getTime())/(24*60*60*1000);
        if(day >7) {
            return false;
        }
        return true;
    }

    // 获取当前时间所在周的开始日期
    public static String getWeekFirstDay(String startDate) {
        Date start = stringToDate(startDate);
        return format(getFirstDayOfWeek(start),YYYY_MM_DD);
    }

    // 获取当前时间所在周的开始日期
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    public static List<String> getSubMonth(String startDate, String endDate) {
        List<String> list = Lists.newArrayList();
        if (startDate.equals(endDate)) {
            return list;
        }
        Date start = stringToDate(startDate);
        Date end = stringToDate(endDate);
        Calendar calendar = Calendar.getInstance();
        while(start.before(end) || start.equals(end)) {
            calendar.setTime(start);
            list.add(format(calendar.getTime(),YYYYMM));
            calendar.add(Calendar.MONTH, 1);
            start = calendar.getTime();
        }
        return list;

    }

    /**
     * @ClassName: DateUtils
     * @Description:
     * @Author: chenDong
     * @Date: 2018/1/8 17:42
     * @Remark: 获取当月天数
     */
    public static Integer getDays(String month) {
        //获取当月天数
        Calendar a = Calendar.getInstance();
        try {
            Date date = DateUtils.format2(month);
            a.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);//最大天数
        return maxDate;

    }

    public static void main(String [] args) {
//        long now = System.currentTimeMillis();
//        System.out.println("当前时间:" + now);
//        long today = getTodayTimeStamp();
//        System.out.println("今天时间:" + today);
//        System.out.println((now - today)/3600000);
        List<String> list = getSubWeek("2016-08-01","2016-08-30");
        System.out.println(getWeekFirstDay("2016-08-02"));
        System.out.println(1);


        Date date = DateUtils.selectMonthLastDay(new Date());
        if(DateUtils.compareDate1(new Date(),date)==1){
            System.out.println("woccccccc");
        }
    }

    /**
     * @ClassName: DateUtils
     * @Description:
     * @Author: chenDong
     * @Date: 2018/1/12 11:38
     * @Remark: 判断当前时间星期几
     */

    public static String checkWeek(Date date){
        String   dateStr   =   "";
        String   weekStr   =   "";
        Calendar   calendar   =   Calendar.getInstance();
        calendar.setTime(date);
        int   week   =   calendar.get(Calendar.DAY_OF_WEEK)-1;
        switch(week){
            case   0:
                weekStr   =   "星期日";
                break;
            case   1:
                weekStr   =   "星期一";
                break;
            case   2:
                weekStr   =   "星期二";
                break;
            case   3:
                weekStr   =   "星期三";
                break;
            case   4:
                weekStr   =   "星期四";
                break;
            case   5:
                weekStr   =   "星期五";
                break;
            case   6:
                weekStr   =   "星期六";
                break;
        }
        return weekStr;
    }

    /**
     * @ClassName: DateUtils
     * @Description:
     * @Author: chenDong
     * @Date: 2018/1/12 14:37
     * @Remark: 获取时间的月初时间
     */
    public static Date selectMonthFirstDay(Date date){
        if(date == null){
            return null;
        }
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(date);
        lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return lastCalendar.getTime();
    }

    /**
     * @ClassName: DateUtils
     * @Description:
     * @Author: chenDong
     * @Date: 2018/1/12 14:37
     * @Remark: 获取时间的月末时间
     */
    public static Date selectMonthLastDay(Date date){
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(date);
        lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
        lastCalendar.roll(Calendar.DATE, -1);
        return lastCalendar.getTime();
    }

    /**
     * 获取当前月的所有日期
     * @param date
     * @return
     */
    public static List<Date> getAllTheDateOftheMonth(Date date) {
        List<Date> list = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        int month = cal.get(Calendar.MONTH);
        while(cal.get(Calendar.MONTH) == month){
            list.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        return list;
    }
    /**
     * 获取当前月的所有日期
     * @param date
     * @return
     */
    public static List<Integer> getAllWeeksOfMonth(Date date) {
        List<Integer> list = new ArrayList<Integer>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        int month = cal.get(Calendar.MONTH);
        while(cal.get(Calendar.MONTH) == month){
            list.add(cal.get(Calendar.DAY_OF_WEEK));
            cal.add(Calendar.DATE, 1);
        }
        return list;
    }

    /*
         俩个时间相差的时间
          */
    public static List<String> getTimeScaleFix(Date startTime, Date endTime, int cycle) {
        startTime=DateUtils.formatDate(startTime,"yyyy-MM-dd");
        endTime=DateUtils.formatDate(endTime,"yyyy-MM-dd");
        List<String> temp = new ArrayList<>();
        Calendar st = Calendar.getInstance();
        Calendar et = Calendar.getInstance();
        st.setTime(startTime);
        et.setTime(endTime);

        if (cycle == Calendar.DAY_OF_YEAR) {
            temp.add(DateUtils.format(startTime, DateUtils.YYYY_MM_DD));
            while (st.before(et)) {
                st.add(Calendar.DAY_OF_YEAR, 1);
                temp.add(DateUtils.format(st.getTime(), DateUtils.YYYY_MM_DD));
            }
        } else  if (cycle == Calendar.WEEK_OF_YEAR){
            temp.add(DateUtils.format(startTime, DateUtils.YYYY_MM_DD));
            Date date=null;
            while (st.before(et)) {
                st.setFirstDayOfWeek(Calendar.MONTH);//设置星期一为第一周
                st.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                st.add(Calendar.WEEK_OF_YEAR, 1);
                date=st.getTime();
                if (date.getTime()<=endTime.getTime()) {
                    temp.add(DateUtils.format(date, DateUtils.YYYY_MM_DD));
                }else{
                    temp.add(DateUtils.format(endTime, DateUtils.YYYY_MM_DD));
                    break;
                }
            }
            if (date==null){
                temp.add(DateUtils.format(endTime, DateUtils.YYYY_MM_DD));
            }
        }else if (cycle == Calendar.MONTH){
            temp.add(DateUtils.format(startTime, DateUtils.YYYY_MM_DD));
            Date date=null;
            while (st.before(et)) {
                st.set(Calendar.DATE, 1);
                st.add(Calendar.MONTH, 1);
                date=st.getTime();
                if (date.getTime()<=endTime.getTime()) {
                    temp.add(DateUtils.format(date, DateUtils.YYYY_MM_DD));
                }else{
                    temp.add(DateUtils.format(endTime, DateUtils.YYYY_MM_DD));
                    break;
                }
            }
            if (date==null){
                temp.add(DateUtils.format(endTime, DateUtils.YYYY_MM_DD));
            }
        }
        return temp;
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(hour==0){
            return min + "分";
        }else {
            if(min==0){
                return hour + "小时";
            }
            return hour + "小时" + min + "分";
        }
        //return day + "," + hour + "," + min + "," + sec + ",";
    }

    /**
     * 取中间时间点
     * @param start
     * @param end
     * @return
     */
    public static String getMiddleTime(Date start,Date end){
       long middle = (start.getTime()+end.getTime())/2;
        start.setTime(middle);
        return format(start,HH_MM);
    }

    /**
     * 某个时间减去mm分钟
     * @param start
     * @param mm
     * @return
     */
    public static Date reduceMm(Date start,int mm){
        long time = mm*60*1000;//分钟
        Date beforeDate = new Date(start.getTime() - time);//30分钟前的时间
        return beforeDate;
    }

    /**
     * 传入分钟，返回分钟加小时
     * @return
     */
    public static String getHhMmByMm(Long mm){
        String resp="";
        long hh=0L;
        hh=mm/60;
        mm=mm%60;
        if (hh>0){
            resp=hh+"小时";
        }
        if (mm>0){
            resp=resp+mm+"分钟";
        }
        return resp;
    }

    /**
     * 保留时间：年月日
     * @param date
     * @return
     */
    public static Date changeFormatYmd(Date date){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }
    /**
     * 拼接两个时间
     * @param starts
     * @param ends
     * @return
     */
    public static Date getSumTime(String starts,String ends){
        String times = starts+" "+ends;
        return DateUtils.stringToDate(times,DateUtils.YYYY_MM_DD_HH_MM);
    }

    /**
     * 自动补0
     * @return
     */
    public static String getWeekNumOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        String zero="";
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int weekNum = calendar.get(Calendar.WEEK_OF_YEAR);
        if (weekNum >10){
             zero="";
        }else {
             zero=0+"";
        }
        return zero+weekNum;
    }
    public static Date getBeginDayOfLastMonth(){
         Calendar nowDateCal = Calendar.getInstance();
         nowDateCal.add(Calendar.MONTH, -1);
         nowDateCal.set(Calendar.DAY_OF_MONTH, 1);
        nowDateCal.set(Calendar.HOUR_OF_DAY, 0);
         nowDateCal.set(Calendar.SECOND,0);
         nowDateCal.set(Calendar.MINUTE,0);
         Date beginDate = nowDateCal.getTime();
         return beginDate;
    }

    public static Date getBeginDayOfThisMonth(){
        Calendar nowDateCal = Calendar.getInstance();
        nowDateCal.add(Calendar.MONTH, 0);
        nowDateCal.set(Calendar.DAY_OF_MONTH, 1);
        nowDateCal.set(Calendar.HOUR_OF_DAY, 0);
        nowDateCal.set(Calendar.SECOND,0);
        nowDateCal.set(Calendar.MINUTE,0);
        Date beginDate = nowDateCal.getTime();
        return beginDate;
    }
    public static Date getEndDayOfLastMonth(){
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
    public static Date getBeginDayOfLastWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek - 7);
        return getDayStartTime(cal.getTime());
    }

    // 获取上周的结束时间
    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfLastWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            boolean isSameMonth =false;
            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                    .get(Calendar.YEAR);
            if (isSameYear){
                isSameMonth = isSameYear
                        && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            }
            return isSameMonth;
        } catch (Exception e) {

        }
        return false;


    }








}
