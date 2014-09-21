package com.fatepay.interfaces;

/**
 * 基础接口
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-17 22:49
 */
public interface BaseInterface {
    /**
     * 前台页面返回URL
     */
    public static final String RETURN_URL= "return_url";
    /**
     * 后台异步通知URL
     */
    public static final String NOTIFY_URL= "notify_url";
    /**
     * 最多异步通知次数
     */
    public static final String MAX_NOTIFY_TIMES= "max_notify_times";
    /**
     * 智付支付地址
     */
    public static final String DINPAY_PAY_URL= "dinpay_pay_url";
    /**
     * U支付在智付商户号
     */
    public static final String DINPAY_MERCHANT_CODE= "dinpay_merchant_code";
    /**
     * 智付md5秘钥
     */
    public static final String DINPAY_MD5KEY= "dinpay_md5Key";
}