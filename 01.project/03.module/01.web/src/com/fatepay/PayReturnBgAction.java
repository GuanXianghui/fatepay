package com.fatepay;

import com.fatepay.api.TransProcessorResult;
import com.fatepay.bank.PayResolverDispatcher;
import com.fatepay.entities.PayRecord;
import com.fatepay.service.IPayRecordService;
import com.fatepay.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 支付返回后台Action
 * @author Gxx
 * @module fatepay
 * @datetime 2014-09-18 21:36
 */
@Component("payReturnBgAction")
@Scope("prototype")
public class PayReturnBgAction extends BaseAction {
    /**
     * 交易网关流水
     */
    String uPayTradeNo;

    /**
     * 支付记录服务类
     */
    private IPayRecordService payRecordService;

    /**
     * 空的构造方法
     */
    public PayReturnBgAction(){}

    /**
     * 入口
     * @return
     */
    public String execute() throws Exception {
        logger.info("uPayTradeNo=" + uPayTradeNo);
        // 判交易网关流水不为空
        if(StringUtils.isEmpty(uPayTradeNo)){
            String resp = "交易网关流水不能为空！";
            write(resp);
            return null;
        }
        // 根据交易网关流水查询支付记录
        PayRecord payRecord = payRecordService.getPayRecordByTradeNo(uPayTradeNo);
        // 判不存在
        if(payRecord == null){
            String resp = "不存在该笔交易网关流水：" + uPayTradeNo;
            write(resp);
            return null;
        }

        /**
         * 加个逻辑：
         * 智付异步通知U支付，如果交易是24小时之前的交易，U支付不处理！
         * 该笔交易原始状态：
         * (1)成功：已经成功，不必接受
         * (2)失败：无法变成成功，等商户把交易流水发过来，我们把流水置成未知，等待我们对账跑批程序
         * (3)初始或者未知：可以等待我们对账跑批程序
         * 以上这么处理的原因是：尽量不让24小时前未结算给商户的交易，自己偷偷变成成功，日终跑对账结算跑批跑不到这些24小时前的交易，
         * 从而漏结算了这些交易金额给商户
         */
        Date yesterday = DateUtil.getYesterday(new Date());
        Date recordDate = DateUtil.getDateTime(payRecord.getCreateDate(), payRecord.getCreateTime());
        if(recordDate.before(yesterday)){
            String resp = "不处理异步通知24小时之前的交易！";
            logger.error(resp);
            write(resp);
            return null;
        }

        // 分发处理器 分析数据
        TransProcessorResult transProcessorResult = PayResolverDispatcher.dispatch().analiseData(payRecord, request, false);
        String resp = transProcessorResult.getMessage();
        write(resp);
        return null;
    }

    public String getUPayTradeNo() {
        return uPayTradeNo;
    }

    public void setUPayTradeNo(String uPayTradeNo) {
        this.uPayTradeNo = uPayTradeNo;
    }

    public IPayRecordService getPayRecordService() {
        return payRecordService;
    }

    @Resource
    public void setPayRecordService(IPayRecordService payRecordService) {
        this.payRecordService = payRecordService;
    }
}
