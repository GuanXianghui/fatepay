package com.fatepay;

import com.fatepay.api.TransProcessorResult;
import com.fatepay.bank.PayResolverDispatcher;
import com.fatepay.entities.PayRecord;
import com.fatepay.interfaces.ProcessorInterface;
import com.fatepay.service.IPayRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
