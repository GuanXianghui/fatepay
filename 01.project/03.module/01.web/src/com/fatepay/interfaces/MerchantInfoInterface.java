package com.fatepay.interfaces;

/**
 * 商户信息接口
 *
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 18:32
 */
public interface MerchantInfoInterface {
    /**
     * 状态
     * 0：审核中
     * 1：审核失败
     * 2：正常
     * 3：锁定
     * 4：注销
     */
    public static final int STATE_CHECKING = 0;
    public static final int STATE_CHECK_FAILED= 1;
    public static final int STATE_NORMAL = 2;
    public static final int STATE_LOCK = 3;
    public static final int STATE_CANCEL = 4;
}
