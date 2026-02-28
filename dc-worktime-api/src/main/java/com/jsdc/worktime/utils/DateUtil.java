package com.jsdc.worktime.utils;


import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * 提供String date localDateTime相互转换方法
 *
 * @Author：jxl
 * @Date：2024/6/13 8:54
 * @FileDesc：
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    /**
     * 将时间格式为 Fri Aug 09 13:40:04 CST 2024  转换年月日时分秒
     * @param timeStr
     * @return
     */
    public static String strTimeConvert(String timeStr){
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz uuuu", Locale.US);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timeStr, inputFormat);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化ZonedDateTime对象为新的字符串格式
        return zonedDateTime.format(outputFormat);
    }


    /**
     * localDateTime转时间戳
     *
     * @param localDateTime
     * @return
     */
    public static Long LocalDateTimeToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * localDateTime转String
     *
     * @param localDateTime
     * @return
     */
    public static String LocalDateTimeToStr(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dtf);
    }

    /**
     * 根据日期获取当天最开始
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime getStartOfDay(LocalDate localDate) {
        //localDate.atStartOfDay();也可以达到相同效果
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    /**
     * 根据日期获取当天最晚时间
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime getEndOfDay(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MAX);
    }

    /**
     * localDate 转localDateTime
     * 此方法可以直接使用localDate.atStartOfDay()获取当天最开始时间
     * 如：2024-06-13T00:00
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * localDate 转localDateTime
     * 指定时分秒
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate, Integer hour, Integer minutes, Integer second) {
        return localDate.atTime(hour, minutes, second);
    }

    /**
     * localDate 转localDateTime
     * 指定时分
     * 如  2024-06-13T15:30
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate, Integer hour, Integer minutes) {
        return localDate.atTime(LocalTime.of(hour, minutes));
    }

    /**
     * LocalDate转时间戳
     *
     * @param localDate
     * @return
     */
    public static Long localDateToLong(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static String localDateToStr(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    /**
     * Date转localDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date转LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date转String
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Date转long
     *
     * @param date
     * @return
     */
    public static Long dateToLong(Date date) {
        return date.getTime();
    }


    public static void main(String[] args) {
        System.err.println(DateUtil.strToLocalDateTime("2023-01-31 00:00:00"));
        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
    }


    /**
     * string转LocalTime
     *
     * @param timeStr
     * @return
     */
    private static LocalTime strToLocalTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(timeStr + ":00", formatter);
    }

    /**
     * string转LocalDate
     *
     * @param timeStr
     * @return
     */
    private static LocalDate strToLocalDate(String timeStr) {
        return LocalDate.parse(timeStr);
    }

    /**
     * string转LocalDateTime
     *
     * @param timeStr
     * @return
     */
    private static LocalDateTime strToLocalDateTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(timeStr, formatter);
    }

    /**
     * String转Date
     *
     * @param timeStr
     * @return
     */
    private static Date strToDate(String timeStr) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Date.from(LocalDateTime.parse(timeStr, dtf).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * long转localDateTime
     *
     * @param milli
     * @return
     */
    public static LocalDateTime longToLocalDateTime(Long milli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault());
    }

    /**
     * long转localDate
     *
     * @param milli
     * @return
     */
    public static LocalDate longToLocalDate(Long milli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault()).toLocalDate();
    }

}
