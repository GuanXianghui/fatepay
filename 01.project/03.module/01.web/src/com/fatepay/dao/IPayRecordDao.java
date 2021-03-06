package com.fatepay.dao;

import com.fatepay.entities.PayRecord;

import java.util.List;

/**
 * 支付记录表DAO
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 15:50
 */
public interface IPayRecordDao {
    /**
     * 根据交易网关流水查询支付记录
     * @param tradeNo
     * @return
     */
    public PayRecord getPayRecordByTradeNo(String tradeNo);
    /**
     * 查询支付记录
     * @param merchantCode
     * @param orderNo
     * @return
     */
    public PayRecord getPayRecord(String merchantCode, String orderNo);

    /**
     * 新增支付记录
     * @param payRecord
     */
    public void save(PayRecord payRecord);

    /**
     * 查询最大的tradeNo
     */
    public String getMaxTradeNo();

    /**
     * 银行返回交易状态修改支付记录
     * @param payRecord
     */
    public void updateTradeState(PayRecord payRecord);

    /**
     * 银行返回交易状态【强制】修改支付记录
     * @param payRecord
     */
    public void forceUpdateTradeState(PayRecord payRecord);

    /**
     * 查询需要异步通知的记录
     * 条件：【支付成功+未完成通知+通知次数小于5】
     * @return
     */
    public List<PayRecord> queryNeedNotifyRecords();

    /**
     * 异步通知商户系统返回结果修改支付记录
     * @param payRecord
     */
    public void updateNotify(PayRecord payRecord);
}
