package com.fatepay.interfaces;

/**
 * 支付记录接口
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 20:47
 */
public interface PayRecordInterface {
    /**
     * 交易状态
     * 0：初始状态
     * 1：交易成功
     * 2：交易失败
     * 3：未知结果
     */
    public static final int STATE_INIT = 0;
    public static final int STATE_SUCCESS= 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_UNKNOWN = 3;

    /**
     * 交易状态描述
     * 0：初始状态
     * 1：交易成功
     * 2：交易失败
     * 3：未知结果
     */
    public static final String STATE_DESC_INIT = "初始状态";
    public static final String STATE_DESC_SUCCESS= "交易成功";
    public static final String STATE_DESC_FAILED = "交易失败";
    public static final String STATE_DESC_UNKNOWN = "未知结果";

    /**
     * 是否已异步通知
     * 0：未通知
     * 1：已通知
     */
    public static final int ALREADY_NOTIFY_NO = 0;
    public static final int ALREADY_NOTIFY_YES = 1;
}
