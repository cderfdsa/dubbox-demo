package com.cyf.base.common.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */


public class TimeUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_ONLY_REGEX = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
    public static final String DATE_TIME_REGEX = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])(\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d)$";
    private static SimpleDateFormat dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间在0-24区间之内
     */
    public static Pattern timeZonePattern = Pattern.compile("(^[0-9]{1}$|^[10-24]{2}$)");

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by the current Date.
     * 返回的是一个long型的毫秒数
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static long timeNow() {
        return Calendar.getInstance().getTime().getTime();
    }


    /**
     * 获取当前系统时间的yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public static String dateNowStr() {
        return parseString(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 把字符串转换成日期对象
     *
     * @param str
     * @param format
     * @return
     */
    public static Date parseDate(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把日期转换成字符串
     *
     * @param
     */
    public static String parseString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否日期格式，如：2017-05-31
     * @param str
     * @return
     */
    public static boolean isDateOnly(String str){
        Matcher mat = Pattern.compile(DATE_ONLY_REGEX).matcher(str);
        return mat.matches();
    }

    /**
     * 判断是否日期或者日期和时间格式，如：2017-05-31 或者 2017-05-31 15:24:31
     * @return
     */
    public static boolean isDateTime(Object obj){
        if(obj == null) return false;
        if(obj instanceof Date){
            return true;
        }
        Matcher mat = Pattern.compile(DATE_TIME_REGEX).matcher(obj.toString());
        return mat.matches();
    }
}
