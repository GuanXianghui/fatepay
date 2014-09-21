package com.fatepay.interfaces;

/**
 * 交易接口
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 14:23
 */
public interface TransInterface extends BaseInterface {
    // 获取token接口
    public static final String SERVICE_CODE_GET_TOKEN = "getToken";
    // 支付接口
    public static final String SERVICE_CODE_PAY = "pay";
    // 单笔交易查询接口
    public static final String SERVICE_CODE_SINGLE_QUERY= "singleQuery";

}
