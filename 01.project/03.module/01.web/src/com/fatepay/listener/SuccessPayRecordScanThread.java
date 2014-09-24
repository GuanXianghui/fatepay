package com.fatepay.listener;

import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.PayRecordInterface;
import com.fatepay.service.IPayRecordService;
import com.fatepay.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 成功支付记录扫描线程
 * 设计说明：
 * 后台启动一个循环线程，每隔60s，扫描库中【支付成功+未完成通知+通知次数小于5】的记录，
 * 后台数据流通知商户系统，每次通知次数+1，商户系统返回SUCCESS（表示通知成功）则记录已通知成功，不再通知！
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-20 11:27
 */
public class SuccessPayRecordScanThread extends Thread {
    /**
     * 日志处理器
     */
    Logger logger = Logger.getLogger(SuccessPayRecordScanThread.class);

    /**
     * 支付记录服务类
     */
    private IPayRecordService payRecordService;

    /**
     * 构造函数
     */
    public SuccessPayRecordScanThread(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        payRecordService = (IPayRecordService)applicationContext.getBean("payRecordService");
    }

    /**
     * 扫描
     */
    public void run(){
        while (!this.isInterrupted()) {//线程未中断执行循环
            try{
                Thread.sleep(60000);//没隔60秒执行一次
            } catch (Exception e){
                logger.error("异常发生", e);
            }
            logger.info("成功支付记录扫描线程，本次循环时间:[" + DateUtil.getLongDateTime(new Date()) + "]");

            // 查询需要异步通知的记录
            List<PayRecord> payRecordList = payRecordService.queryNeedNotifyRecords();
            logger.info("查询需要异步通知的记录条数：" + payRecordList.size());

            // 逐条通知
            for(PayRecord payRecord : payRecordList){
                logger.info("当前循环支付记录：" + payRecord);
                try{
                    // 返回U支付订单时间
                    String tradeDateTime = payRecord.getTradeDateTime();
                    if(StringUtils.isBlank(tradeDateTime)){
                        tradeDateTime = payRecord.getCreateDate() + payRecord.getCreateTime();
                    }

                    // 组织返回数据集合
                    String returnServiceCode = "payResultNotify";
                    String returnOrderNo = payRecord.getOrderNo();
                    String returnOrderAmount = payRecord.getOrderAmount();
                    String returnTradeNo = StringUtils.trimToEmpty(payRecord.getTradeNo());
                    String returnTradeDateTime = tradeDateTime;
                    String returnTradeState = BaseUtil.translatePayRecordState(payRecord.getTradeState());
                    String returnTradeDesc = BaseUtil.translatePayRecordStateDesc(payRecord.getTradeState());
                    String returnExtraReturnParam = payRecord.getExtraReturnParam();
                    String returnSignType = "MD5";
                    String returnSign = new MD5Util().md5(returnServiceCode + returnOrderNo + returnOrderAmount +
                            returnTradeNo + returnTradeDateTime + returnTradeState + returnTradeDesc + returnExtraReturnParam +
                            returnSignType + MerchantInfoUtil.getInstance().getMerchantInfo(payRecord.getMerchantCode()).getMd5Key());
                    Properties properties = new Properties();
                    properties.put("serviceCode", returnServiceCode);
                    properties.put("orderNo", returnOrderNo);
                    properties.put("orderAmount", returnOrderAmount);
                    properties.put("tradeNo", returnTradeNo);
                    properties.put("tradeDateTime", returnTradeDateTime);
                    properties.put("tradeState", returnTradeState);
                    properties.put("tradeDesc", returnTradeDesc);
                    properties.put("extraReturnParam", returnExtraReturnParam);
                    properties.put("signType", returnSignType);
                    properties.put("sign", returnSign);

                    // 通知商户系统
                    String response = HttpClientUtils.postProps(payRecord.getNotifyUrl(), properties, "UTF-8", "UTF-8");
                    logger.info("通知商户系统返回信息：[" + response + "]");

                    // 判结果
                    if(StringUtils.equalsIgnoreCase("SUCCESS", response)){
                        payRecord.setAlreadyNotify(PayRecordInterface.ALREADY_NOTIFY_YES);
                    }
                    payRecord.setNotifyTimes(payRecord.getNotifyTimes() + 1);
                    payRecord.setUpdateDate(DateUtil.getNowDate());
                    payRecord.setUpdateTime(DateUtil.getNowTime());

                    // 异步通知商户系统返回结果修改支付记录
                    payRecordService.updateNotify(payRecord);
                } catch (Exception e){
                    logger.error("异步通知异常发生！", e);
                    payRecord.setNotifyTimes(payRecord.getNotifyTimes() + 1);
                    payRecord.setUpdateDate(DateUtil.getNowDate());
                    payRecord.setUpdateTime(DateUtil.getNowTime());

                    // 异步通知商户系统返回结果修改支付记录
                    payRecordService.updateNotify(payRecord);
                }
            }
        }
    }
}
