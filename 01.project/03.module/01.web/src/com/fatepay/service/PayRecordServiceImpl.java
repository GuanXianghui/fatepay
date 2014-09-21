package com.fatepay.service;

import com.fatepay.dao.IPayRecordDao;
import com.fatepay.entities.PayRecord;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付记录服务实现类
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-19 15:42
 */
@Component("payRecordService")
public class PayRecordServiceImpl implements IPayRecordService {
    //支付记录表DAO
    private IPayRecordDao payRecordDao;

    /**
     * 无参构造函数
     */
    public PayRecordServiceImpl(){}

    /**
     * 根据交易网关流水查询支付记录
     * @param tradeNo
     * @return
     */
    public PayRecord getPayRecordByTradeNo(String tradeNo){
        return payRecordDao.getPayRecordByTradeNo(tradeNo);
    }

    /**
     * 查询支付记录
     * @param merchantCode
     * @param orderNo
     * @return
     */
    public PayRecord getPayRecord(String merchantCode, String orderNo){
        return payRecordDao.getPayRecord(merchantCode, orderNo);
    }

    /**
     * 新增支付记录
     * @param payRecord
     */
    public void save(PayRecord payRecord){
        payRecordDao.save(payRecord);
    }

    /**
     * 查询最大的tradeNo
     */
    public String getMaxTradeNo(){
        return payRecordDao.getMaxTradeNo();
    }

    /**
     * 银行返回交易状态修改支付记录
     * @param payRecord
     */
    public void updateTradeState(PayRecord payRecord){
        payRecordDao.updateTradeState(payRecord);
    }

    /**
     * 查询需要异步通知的记录
     * 条件：【支付成功+未完成通知+通知次数小于5】
     * @return
     */
    public List<PayRecord> queryNeedNotifyRecords(){
        return payRecordDao.queryNeedNotifyRecords();
    }

    /**
     * 异步通知商户系统返回结果修改支付记录
     * @param payRecord
     */
    public void updateNotify(PayRecord payRecord){
        payRecordDao.updateNotify(payRecord);
    }

    public IPayRecordDao getPayRecordDao() {
        return payRecordDao;
    }

    @Resource
    public void setPayRecordDao(IPayRecordDao payRecordDao) {
        this.payRecordDao = payRecordDao;
    }
}
