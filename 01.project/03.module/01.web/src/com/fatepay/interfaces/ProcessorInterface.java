package com.fatepay.interfaces;

/**
 * 处理器接口
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 19:54
 */
public interface ProcessorInterface {
    // 成功 字符串输出
    public static final String RESULT_TYPE_SUCCESS_STRING = "SUCCESS_STRING";
    // 失败 字符串输出
    public static final String RESULT_TYPE_FAIL_STRING = "FAIL_STRING";
    // 成功 页面跳转到其他系统
    public static final String RESULT_TYPE_SUCCESS_JUMP = "SUCCESS_JUMP";
    // 失败 页面跳转到其他系统
    public static final String RESULT_TYPE_FAIL_JUMP = "FAIL_JUMP";
    // 成功 U支付页面显示结果
    public static final String RESULT_TYPE_SUCCESS_PAGE_RESULT = "SUCCESS_PAGE_RESULT";
    // 失败 U支付页面显示结果
    public static final String RESULT_TYPE_FAIL_PAGE_RESULT = "FAIL_PAGE_RESULT";
}
