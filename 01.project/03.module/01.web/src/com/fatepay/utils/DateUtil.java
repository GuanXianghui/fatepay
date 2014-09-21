package com.fatepay.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-17 23:56
 */
public class DateUtil {
    /**
     * 日志处理器
     */
    static Logger logger = Logger.getLogger(DateUtil.class);

    /**
     * 格式化时间为Date类型
     *
     * @param srcDate    原时间
     * @param srcPattern 原模式
     * @return 格式化后时间
     */
    public static Date parseDateTime(String srcDate, String srcPattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(srcPattern);
            return sdf.parse(srcDate);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }


    /**
     * 格式化时间为字符串类型
     *
     * @param srcDate    原时间
     * @param srcPattern 原模式
     * @param dstPattern 目标模式
     * @return 格式化后时间
     */
    public static String formatDateTime(Object srcDate, String srcPattern, String dstPattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(srcPattern == null ? "yyyyMMdd" : srcPattern);
            Date _date = null;
            if (srcDate == null) {
                return "";
            }
            if (srcDate instanceof String) {
                _date = sdf.parse(srcDate.toString());
            } else if (srcDate instanceof Date) {
                _date = (Date) srcDate;
            }
            sdf = new SimpleDateFormat(dstPattern);
            return sdf.format(_date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将日期字符串转换为Date
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (StringUtils.isBlank(date)) {
            return null;
        }
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将dateTime转换成date
     *
     * @param dateTime 时间yyyyMMddHHmmss
     * @return Date
     */
    public static Date getDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        SimpleDateFormat sdf;
        if (dateTime.indexOf("-") > 0) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        }
        try {
            sdf.setLenient(false);
            return sdf.parse(dateTime);
        } catch (ParseException e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将时间转换成date
     *
     * @param time 字符串式时间HHmmss
     * @return DATE
     */
    public static Date getTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        if (StringUtils.isBlank(time)) {
            return null;
        }
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将日期转换成标准格式yyyy-mm-dd hh:mm:ss
     *
     * @param dateTime 时间
     * @return 标准格式字符串
     */
    public static String getDefaultDateTime(Date dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.format(dateTime);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }


    /**
     * 将日期转成长字符串
     *
     * @param dateTime dateTime请求字符串
     * @return 支付渠道请求日期字符串
     */
    public static String getDateTime(Date dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return sdf.format(dateTime);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将日期和时间转换成date
     *
     * @param date date请求字符串
     * @param time time请求字符串
     * @return 支付渠道请求日期字符串
     */
    public static Date getDateTime(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return sdf.parse(date + time);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将日期转换为请求字符串
     *
     * @param date 日期
     * @return 请求字符串
     */
    public static String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (date == null) {
            return "";
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return "";
        }
    }

    /**
     * 将日期转换为短字符串，格式：yyMMdd
     *
     * @param date 日期
     * @return 字符串
     */
    public static String getShortDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        if (date == null) {
            return null;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将日期转换为短字符串，格式：yyyy-MM-dd
     *
     * @param date 日期
     * @return 字符串
     */
    public static String getLongDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (date == null) {
            return null;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将时间转换为请求字符串
     *
     * @param date 时间
     * @return 请求字符串
     */
    public static String getTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        if (date == null) {
            return "";
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return "";
        }
    }

    /**
     * 将时间转换为请求字符串
     *
     * @param date 时间
     * @return 请求字符串
     */
    public static String getLongTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (date == null) {
            return "";
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return "";
        }
    }

    /**
     * 将日期转换为长字符串，格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return 字符串
     */
    public static String getLongDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date == null) {
            return null;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将长字符串转换为日期，格式：yyyy-MM-dd HH:mm:ss
     *
     * @param longDateTime 日期时间长字符串
     * @return 字符串
     */
    public static Date getLongDateTime(String longDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(longDateTime);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将日期字符串转换为Date自然时间
     *
     * @param date 日期字符串
     * @return 自然时间
     */
    public static String getFreeTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
        if (date == null) {
            return null;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 将日期字符串转换为Date自然日期的日
     *
     * @param date 日期字符串
     * @return 自然日期的日
     */
    public static String getFreeDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        if (date == null) {
            return null;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return null;
        }
    }

    /**
     * 转换成中国汉语日期
     *
     * @param date
     * @return
     */
    public static String getCNDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        if (date == null) {
            return StringUtils.EMPTY;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 转换成中国汉语日期时间
     *
     * @param date
     * @return
     */
    public static String getCNDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        if (date == null) {
            return StringUtils.EMPTY;
        }
        try {
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("异常发生~", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 得到日期的前一天
     *
     * @param date
     * @return
     */
    public static Date getYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        return yesterday;
    }

    /**
     * 得到当前日期
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 得到当前日期
     *
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(new Date());
    }
}
