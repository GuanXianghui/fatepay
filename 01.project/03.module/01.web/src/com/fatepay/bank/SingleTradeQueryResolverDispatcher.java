package com.fatepay.bank;

import com.fatepay.bank.dinpay.DpSingleTradeQueryResolver;

/**
 * 单笔交易查询处理器分发
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-22 14:15
 */
public class SingleTradeQueryResolverDispatcher {
    /**
     * 分发处理器 这里暂不做分支 统一跳到智付
     * @return
     */
    public static ISingleTradeQueryResolver dispatch(){
        return new DpSingleTradeQueryResolver();
    }
}
