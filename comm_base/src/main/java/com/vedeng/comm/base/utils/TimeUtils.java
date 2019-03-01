package com.vedeng.comm.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2016/12/21 9:43
 * @文件描述：时间日期相关的工具类
 * @修改历史：2016/12/21 创建初始版本
 **********************************************************/

public class TimeUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static final SimpleDateFormat dateFormatWithMillis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS Z");

    /**
     * 将当前日期转出GMT格式字符串
     *
     * @param date
     * @return
     */
    public static String toGMTString(Date date) {
        return dateFormatWithMillis.format(date);
    }

    /**
     * 根据当前日期，获取该日期下00:00:00 000的日期
     *
     * @param date
     * @return
     */
    public static Date getStartTimeOfOneDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 将GMT格式的字符串转成日期
     *
     * @param dateGMT
     * @return
     */
    public static Date transformGMTtoDate(String dateGMT) {
        try {
            return dateFormatWithMillis.parse(dateGMT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前系统时区
     *
     * @return
     */
    public static TimeZone getCurrentTimeZone() {
        return TimeZone.getTimeZone(getCurrentTimeZoneStr());
    }

    private static String getCurrentTimeZoneStr() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(false, false, tz.getRawOffset());
    }

    private static String createGmtOffsetString(boolean includeGmt,
                                                boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    public static String toGMTStringWithMillis(Date date) {
        return dateFormatWithMillis.format(date);
    }
}
