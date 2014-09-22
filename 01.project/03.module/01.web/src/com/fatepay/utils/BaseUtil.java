package com.fatepay.utils;

import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.interfaces.SymbolInterface;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Properties;

/**
 * 基础工具类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 00:04
 */
public class BaseUtil {
    /**
     * 日志处理器
     */
    static Logger logger = Logger.getLogger(BaseUtil.class);

    /**
     * 翻译支付记录状态给商户
     * @param state
     * @return
     */
    public static String translatePayRecordState(int state){
        // 成功
        if(PayRecordInterface.STATE_SUCCESS == state){
            return "SUCCESS";
        }
        // 失败
        if(PayRecordInterface.STATE_FAILED == state){
            return "FAILED";
        }
        // 初始化 或者 未知
        return "UNKNOWN";
    }

    /**
     * 翻译支付记录状态中文
     * @param state
     * @return
     */
    public static String translatePayRecordStateDesc(int state){
        // 成功
        if(PayRecordInterface.STATE_SUCCESS == state){
            return PayRecordInterface.STATE_DESC_SUCCESS;
        }
        // 失败
        if(PayRecordInterface.STATE_FAILED == state){
            return PayRecordInterface.STATE_DESC_FAILED;
        }
        // 初始化 或者 未知
        return PayRecordInterface.STATE_DESC_UNKNOWN;
    }

    /**
     * 打印出properties对象
     * @param properties
     */
    public static void loggerOut(Properties properties){
        String string = StringUtils.EMPTY;
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            String value = properties.getProperty(key);
            if(StringUtils.isNotBlank(string)){
                string += SymbolInterface.SYMBOL_COMMA;
            }
            string += key + ":[" + value + "]";
        }
        logger.info(string);
    }

    /**
     * 将元格式化 元精确到小数点后两位
     * @param money
     * @return
     */
    public static String formatYuan(String money){
        DecimalFormat decimalFormat = new DecimalFormat("#######0.00");
        return decimalFormat.format(Double.parseDouble(money));
    }

//    /**
//     * 将分转成元 元精确到小数点后两位
//     * @param money
//     * @return
//     */
//    public static String translateFenToYuan(String money){
//        DecimalFormat decimalFormat = new DecimalFormat("#######0.00");
//        return decimalFormat.format(Integer.parseInt(money)*1.0/100);
//    }
//
//    /**
//     * 将元转成分 元精确到小数点后两位
//     * @param money
//     * @return
//     */
//    public static String translateYuanToFen(String money){
//        DecimalFormat decimalFormat = new DecimalFormat("#########0");
//        return decimalFormat.format(Double.parseDouble(money) * 100);
//    }

    /**
     * 为支付状态判优先级
     * 优先级是：初始<未知<失败<成功
     * 新状态优先级 大于 旧状态 返回true
     * 否则 返回false
     * @param newState 新状态
     * @param oldState 旧状态
     * @return
     */
    public static boolean comparePriorityForPayState(int newState, int oldState){
        if(newState == PayRecordInterface.STATE_SUCCESS && oldState != PayRecordInterface.STATE_SUCCESS){
            return true;
        }
        if(newState == PayRecordInterface.STATE_FAILED &&
                (oldState == PayRecordInterface.STATE_UNKNOWN || oldState == PayRecordInterface.STATE_INIT)){
            return true;
        }
        if(newState == PayRecordInterface.STATE_UNKNOWN && oldState == PayRecordInterface.STATE_INIT){
            return true;
        }
        return false;
    }

    /**
     * 如果ojb为null，返回空字符串，否则原样返回obj
     * @param obj
     * @return
     */
    public static Object setNull2Empty(Object obj){
        if(null == obj){
            return StringUtils.EMPTY;
        }
        return obj;
    }
}
