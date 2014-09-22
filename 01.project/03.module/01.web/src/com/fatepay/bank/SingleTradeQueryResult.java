package com.fatepay.bank;

/**
 * 单笔交易查询结果
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-22 14:08
 */
public class SingleTradeQueryResult {
    /**
     * 查询是否成功
     * true 成功
     * false 失败
     */
    boolean isQuerySuccess;
    /**
     * 交易是否存在
     * true 存在
     * false 不存在
     */
    boolean isExist;
    /**
     * 交易结果
     * 1：交易成功 PayRecordInterface.STATE_SUCCESS
     * 2：交易失败 PayRecordInterface.STATE_FAILED
     * 3：未知结果 PayRecordInterface.STATE_UNKNOWN
     */
    int tradeState;
    /**
     * 银行交易时间 格式：yyyyMMddHHmmss
     */
    String tradeDateTime;
    /**
     * 银行流水
     */
    String tradeBankSeq;

    public boolean isQuerySuccess() {
        return isQuerySuccess;
    }

    public void setQuerySuccess(boolean querySuccess) {
        isQuerySuccess = querySuccess;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public int getTradeState() {
        return tradeState;
    }

    public void setTradeState(int tradeState) {
        this.tradeState = tradeState;
    }

    public String getTradeDateTime() {
        return tradeDateTime;
    }

    public void setTradeDateTime(String tradeDateTime) {
        this.tradeDateTime = tradeDateTime;
    }

    public String getTradeBankSeq() {
        return tradeBankSeq;
    }

    public void setTradeBankSeq(String tradeBankSeq) {
        this.tradeBankSeq = tradeBankSeq;
    }
}
