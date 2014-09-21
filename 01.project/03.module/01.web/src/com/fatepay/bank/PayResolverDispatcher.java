package com.fatepay.bank;

import com.fatepay.bank.dinpay.DpPayResolver;

/**
 * 支付处理器分发
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 14:30
 */
public class PayResolverDispatcher {
    /**
     * 分发处理器 这里暂不做分支 统一跳到智付
     * @return
     */
    public static IPayResolver dispatch(){
        return new DpPayResolver();
    }
}
