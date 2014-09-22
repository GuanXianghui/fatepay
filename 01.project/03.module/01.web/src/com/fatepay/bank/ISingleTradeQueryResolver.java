package com.fatepay.bank;

import com.fatepay.entities.PayRecord;

/**
 * 单笔交易查询接口
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-22 13:23
 */
public interface ISingleTradeQueryResolver {
    /**
     * 查询交易
     * @param payRecord 支付记录
     * @return 单笔交易查询结果
     */
    public SingleTradeQueryResult query(PayRecord payRecord);
}
