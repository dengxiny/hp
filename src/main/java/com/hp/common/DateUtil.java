package com.hp.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateUtil {
    public static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String formatDate(Date date, String format) {
        if (date == null || format == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static Date parseDate(String date, String format) {
        if (date == null || format == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取当前时间的字符串形式。时间格式：yyyyMMddHHmmss
     */
    public static String getCurrentTimeString() {
        return defaultFormat.format(new Date());
    }

    /**
     * 时间转换
     *
     * @param date 需要转换的时间
     * @return 转换后时间
     */
    public static String formatTo_yyyyMMddHHmmss(Date date) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }

    /**
     * 获取当前时间的字符串形式。时间格式：yyyyMMdd
     * @return
     */
    public static String formatTo_yyyyMMdd(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 获取当前年份
     * @return
     */
    public static String formatTo_yyyy(){
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    /**
     * 获取当前时间精确到毫秒数。时间格式为yyyyMMddHHmmssSSS
     * @return
     */
    public static String formatTo_yyyyMMddHHmmssSSS(){
        return  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }



}
